package Servlet;

import Entity.Place;
import Entity.Photo;
import Service.PhotoService;
import Service.PlaceService;
import Service.impl.PhotoServiceImpl;
import Service.impl.PlaceServiceImpl;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1MB缓存阈值
        maxFileSize = 5 * 1024 * 1024,   // 单个文件最大5MB
        maxRequestSize = 20 * 1024 * 1024 // 总请求最大20MB
)
@WebServlet("/AddPlace")
public class AddPlaceServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "upload"; // 与攻略图片同一存储目录
    private final PlaceService placeService = new PlaceServiceImpl();
    private final PhotoService photoService = new PhotoServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取前端参数
            String placeName = req.getParameter("placeName");
            String openTime = req.getParameter("placeOpenTime");
            String placeIntro = req.getParameter("placeIntro");

            // 2. 校验必填参数
            if (placeName == null || placeName.trim().isEmpty()) {
                throw new SQLException("地点名称不能为空");
            }

            // 3. 检查地点是否已存在（通过Service层转发到DAO层查询）
            if (placeService.isPlaceNameExists(placeName.trim())) {
                throw new SQLException("地点“" + placeName + "”已存在，无需重复添加");
            }

            // 4. 构造Place实体（无需设置placeId，由DAO层生成）
            Place place = new Place();
            place.setPlaceName(placeName.trim());
            place.setOpenTime(openTime);
            place.setPlaceIntro(placeIntro);

            // 5. 调用Service层插入（DAO层内部生成ID并写入数据库）
            Place savedPlace = placeService.insert(place);
            String placeId = savedPlace.getPlaceId(); // 从插入后的实体中获取DAO层生成的ID

            // 6. 批量处理地点图片上传（不变）
            int imageCount = 0;
            for (int i = 0; ; i++) {
                Part imagePart = req.getPart("placeImages" + i);
                if (imagePart == null || imagePart.getSize() == 0) {
                    break;
                }

                String imageDesc = req.getParameter("placeImageDescs" + i);
                if (imageDesc == null) imageDesc = "";

                // 保存图片文件
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
                System.out.println("地点图片保存路径：" + savePath);

                // 存储图片信息到数据库（共用Photo表）
                Photo photo = new Photo();
                photo.setDescribe(imageDesc.trim());
                photo.setTipId(placeId); // 关联DAO层生成的placeId
                photo.setPath(UPLOAD_DIR + "/" + uniqueFileName);
                photoService.uploadPhoto(photo);

                imageCount++;
            }

            // 7. 响应结果
            result.put("success", true);
            result.put("placeId", placeId); // 返回DAO层生成的ID
            result.put("imageCount", imageCount);
            result.put("message", "地点添加成功！地点ID：" + placeId + "，共上传" + imageCount + "张图片");

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

    // 解析文件名工具方法（不变）
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