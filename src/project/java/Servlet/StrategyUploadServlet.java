package Servlet;

import DAO.impl.PlaceDAOImpl;
import DAO.impl.RouteDAOImpl;
import DAO.impl.StrategyDAOImpl;
import Entity.*;
import Service.PhotoService;
import Service.StrategyService;
import Service.impl.PhotoServiceImpl;
import Service.impl.StrategyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1MB缓存阈值
        maxFileSize = 5 * 1024 * 1024,   // 单个文件最大5MB
        maxRequestSize = 20 * 1024 * 1024 // 总请求最大20MB
)
@WebServlet("/StrategyWithPhotoUpload")
public class StrategyUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "upload";
    private final StrategyService strategyService = new StrategyServiceImpl();
    private final PhotoService photoService = new PhotoServiceImpl();
    private final StrategyDAOImpl strategyDAO = new StrategyDAOImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlaceDAOImpl placeDAO = new PlaceDAOImpl();
    private final RouteDAOImpl routeDAO = new RouteDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取攻略文本参数
            String title = req.getParameter("strategyTitle");
            String content = req.getParameter("strategyContent");
            String userId = req.getParameter("userAccount"); // 提前获取用户ID

            // 校验用户ID必填
            if (userId == null || userId.trim().isEmpty()) {
                throw new SQLException("发布用户ID不能为空");
            }

            // 2. 校验攻略必填参数
            if (title == null || title.trim().isEmpty()) {
                throw new SQLException("攻略标题不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new SQLException("攻略内容不能为空");
            }

            // 3. 生成攻略唯一tipId并保存攻略
            String tipId = strategyDAO.generateUniqueTipid();
            Strategy strategy = new Strategy();
            strategy.setTipId(tipId);
            strategy.setTitle(title.trim());
            strategy.setContent(content.trim());
            strategy.setS_userId(userId.trim());
            strategy.setCreateTime(new Date());
            strategyService.insert(strategy); // 先保存攻略，确保关联时记录已存在

            // 4. 处理地点信息
            int placeCount = Integer.parseInt(req.getParameter("placeCount"));
            for (int i = 0; i < placeCount; i++) {
                Place place = new Place();
                String placeId = placeDAO.generateUniquePlaceId();

                String placeName = req.getParameter("places[" + i + "].placeName");
                String openTime = req.getParameter("places[" + i + "].openTime");
                String placeIntro = req.getParameter("places[" + i + "].intro");
                String amapPoiId = req.getParameter("places[" + i + "].amapPoiId");

                if (placeName == null || placeName.trim().isEmpty()) {
                    throw new SQLException("第" + (i + 1) + "个地点名称不能为空");
                }

                place.setPlaceId(placeId);
                place.setPlaceName(placeName.trim());
                place.setOpenTime(openTime != null ? openTime.trim() : "");
                place.setPlaceIntro(placeIntro != null ? placeIntro.trim() : "");
                place.setP_tipId(tipId);
                placeDAO.insert(place);

                // 处理地点图片
                int placeImageCount = Integer.parseInt(req.getParameter("places[" + i + "].imageCount"));
                for (int j = 0; j < placeImageCount; j++) {
                    Part placeImagePart = req.getPart("places[" + i + "].images[" + j + "]");
                    if (placeImagePart == null || placeImagePart.getSize() == 0) {
                        continue;
                    }
                    String originalFileName = getFileName(placeImagePart);
                    String fileExt = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String uniqueFileName = UUID.randomUUID().toString() + fileExt;
                    String realPath = getServletContext().getRealPath("/" + UPLOAD_DIR);
                    File uploadDir = new File(realPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    String savePath = realPath + File.separator + uniqueFileName;
                    placeImagePart.write(savePath);

                    Photo placePhoto = new Photo();
                    placePhoto.setDescribe("");
                    placePhoto.setTipId(tipId);
                    placePhoto.setPath(UPLOAD_DIR + "/" + uniqueFileName);
                    photoService.uploadPhoto(placePhoto);
                }
            }

            // 5. 处理路线信息
            String routeId = null;
            int routeCount = Integer.parseInt(req.getParameter("routeCount"));

            Route route = new Route();
            routeId = routeDAO.generateUniqueRouteId();

            String routeTitle = req.getParameter("routes[0].routeTitle");
            String routeDayStr = req.getParameter("routes[0].routeDay");

            if (routeTitle == null || routeTitle.trim().isEmpty()) {
                throw new SQLException("路线标题不能为空");
            }
            if (routeDayStr == null || routeDayStr.trim().isEmpty() || !routeDayStr.matches("\\d+")) {
                throw new SQLException("路线天数必须是正整数");
            }
            Integer routeDay = Integer.parseInt(routeDayStr);

            route.setRouteId(routeId);
            route.setR_title(routeTitle.trim());
            route.setR_day(routeDay);
            route.setR_tipId(tipId);
            routeDAO.addRoute(route);

            // 6. 处理攻略图片
            int imageCount = 0;
            for (int i = 0; ; i++) {
                Part imagePart = req.getPart("strategyImages" + i);
                if (imagePart == null || imagePart.getSize() == 0) {
                    break;
                }
                String imageDesc = req.getParameter("strategyImageDescs" + i);
                if (imageDesc == null) imageDesc = "";

                String realPath = getServletContext().getRealPath("/" + UPLOAD_DIR);
                File uploadDir = new File(realPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                    System.out.println("已创建upload目录：" + realPath);
                }
                String originalFileName = getFileName(imagePart);
                String fileExt = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExt;
                String savePath = realPath + File.separator + uniqueFileName;
                imagePart.write(savePath);
                System.out.println("图片实际保存路径：" + savePath);

                Photo photo = new Photo();
                photo.setDescribe(imageDesc.trim());
                photo.setTipId(tipId);
                photo.setPath(UPLOAD_DIR + "/" + uniqueFileName);
                photoService.uploadPhoto(photo);

                imageCount++;
            }
            // 8. 响应成功结果
            result.put("success", true);
            result.put("tipId", tipId);
            result.put("imageCount", imageCount);
            result.put("message", String.format("攻略发布成功！攻略ID：%s，共上传%d张图片，添加%d个地点，添加%d条路线",
                    tipId, imageCount, placeCount, routeCount));

        } catch (SQLException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务器错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            out.write(objectMapper.writeValueAsString(result));
            out.close();
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String item : contentDisposition.split(";")) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "unknown_file";
    }
}