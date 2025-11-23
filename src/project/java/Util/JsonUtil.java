package Util;

import com.alibaba.fastjson.JSON;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JSON处理工具类
 */
public class JsonUtil {
    /**
     * 解析JSON请求为Java对象
     */
    public static <T> T parseRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return JSON.parseObject(sb.toString(), clazz);
    }

    /**
     * 发送JSON响应
     */
    public static void writeResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(data));
        out.flush();
        out.close();
    }

    /**
     * 构建成功响应（统一格式）
     */
    public static SuccessResponse buildSuccess(String message, Object data) {
        return new SuccessResponse(true, message, data);
    }

    /**
     * 构建失败响应（统一格式）
     */
    public static ErrorResponse buildError(String message) {
        return new ErrorResponse(false, message);
    }

    // 成功响应实体
    public static class SuccessResponse {
        private boolean success;
        private String message;
        private Object data;

        public SuccessResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getter
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
    }

    // 失败响应实体
    public static class ErrorResponse {
        private boolean success;
        private String message;

        public ErrorResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getter
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}