package zyhinventory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * API客户端类
 * 负责与远程服务器通信，获取和提交库存数据
 * 服务器地址：http://10.122.18.142:38080
 *
 * 注意：已改为纯本地模式，API代码已注释保留。
 * 数据从 data/ 目录的JSON文件读取（ZYHLocalData）。
 * 如需恢复远程API模式，取消注释相关代码即可。
 */
public class ZYHApiClient {
    /** 数据查询接口URL */
    private static final String DATA_URL = "http://10.122.18.142:38080/data";

    /** 数据修改接口URL */
    private static final String CHANGE_URL = "http://10.122.18.142:38080/change";

    /**
     * 查询数据（带完整参数）
     * @param type 数据类型（商品/库存/入库/出库）
     * @param filter 筛选条件（JSON格式）
     * @param date 日期筛选
     * @param period 时间段筛选
     * @return 服务器返回的JSON数据
     */
    public static String queryData(String type, String filter, String date, String period) {
        /* === 原始API代码 - 已注释，改为本地模式 ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"").append(type).append("\"");

        if (filter != null && !filter.isEmpty()) {
            jsonBuilder.append(",\"filter\":").append(filter);
        }

        if (date != null && !date.isEmpty()) {
            jsonBuilder.append(",\"date\":\"").append(date).append("\"");
        }

        if (period != null && !period.isEmpty()) {
            jsonBuilder.append(",\"period\":\"").append(period).append("\"");
        }

        jsonBuilder.append("}");

        return sendPostRequest(DATA_URL, jsonBuilder.toString());
        === 原始API代码结束 === */

        // 本地模式：直接从JSON文件读取
        return ZYHLocalData.queryData(type);
    }

    /**
     * 查询数据（简单版本）
     * @param type 数据类型
     * @return 服务器返回的JSON数据
     */
    public static String queryData(String type) {
        /* === 原始API代码 - 已注释，改为本地模式 ===
        return queryData(type, null, null, null);
        === 原始API代码结束 === */

        // 本地模式：直接从JSON文件读取
        return ZYHLocalData.queryData(type);
    }

    /**
     * 修改库存商品信息
     * @param action 操作类型（add新增/modify修改/delete删除）
     * @param recordId 记录ID
     * @param name 商品名称
     * @param barcode 商品条码
     * @param buyPrice 进货价格
     * @param sellPrice 销售价格
     * @return 服务器返回的JSON数据
     */
    public static String modifyInventory(String action, String recordId, String name,
            String barcode, Double buyPrice, Double sellPrice) {
        /* === 原始API代码 - 已注释，改为只读模式 ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"库存\",\"action\":\"").append(action).append("\"");

        if (recordId != null && !recordId.isEmpty()) {
            jsonBuilder.append(",\"record_id\":\"").append(recordId).append("\"");
        }

        if (barcode != null && !barcode.isEmpty()) {
            jsonBuilder.append(",\"商品条码\":\"").append(barcode).append("\"");
        }

        if (name != null && !name.isEmpty()) {
            jsonBuilder.append(",\"商品名称\":\"").append(name).append("\"");
        }

        if (buyPrice != null) {
            jsonBuilder.append(",\"进货价格\":").append(buyPrice);
        }

        if (sellPrice != null) {
            jsonBuilder.append(",\"售卖定价\":").append(sellPrice);
        }

        jsonBuilder.append("}");

        return sendPostRequest(CHANGE_URL, jsonBuilder.toString());
        === 原始API代码结束 === */

        // 只读模式：返回成功，操作仅在内存中生效
        return "{\"success\":true,\"message\":\"本地只读模式，操作仅在内存中生效\"}";
    }

    /**
     * 修改入库/出库记录
     * @param type 类型（入库/出库）
     * @param action 操作类型
     * @param recordId 记录ID
     * @param barcode 商品条码
     * @param quantity 数量
     * @param date 日期
     * @param itemName 商品名称
     * @return 服务器返回的JSON数据
     */
    public static String modifyStock(String type, String action, String recordId,
            String barcode, Integer quantity, String date, String itemName) {
        /* === 原始API代码 - 已注释，改为只读模式 ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"").append(type).append("\",\"action\":\"").append(action).append("\"");

        if (recordId != null && !recordId.isEmpty()) {
            jsonBuilder.append(",\"record_id\":\"").append(recordId).append("\"");
        }

        if (barcode != null && !barcode.isEmpty()) {
            jsonBuilder.append(",\"商品条码\":\"").append(barcode).append("\"");
        }

        if (quantity != null) {
            jsonBuilder.append(",\"数量\":").append(quantity);
        }

        if (date != null && !date.isEmpty()) {
            jsonBuilder.append(",\"日期\":\"").append(date).append("\"");
        }

        if (itemName != null && !itemName.isEmpty()) {
            if ("入库".equals(type)) {
                jsonBuilder.append(",\"入库项\":\"").append(itemName).append("\"");
            } else if ("出库".equals(type)) {
                jsonBuilder.append(",\"出库项\":\"").append(itemName).append("\"");
            }
        }

        jsonBuilder.append("}");

        return sendPostRequest(CHANGE_URL, jsonBuilder.toString());
        === 原始API代码结束 === */

        // 只读模式：返回成功，操作仅在内存中生效
        return "{\"success\":true,\"message\":\"本地只读模式，操作仅在内存中生效\"}";
    }

    /**
     * 重置所有数据
     * @return 服务器返回的JSON数据
     */
    public static String resetData() {
        /* === 原始API代码 - 已注释，改为只读模式 ===
        return sendPostRequest(CHANGE_URL, "{\"action\":\"reset\"}");
        === 原始API代码结束 === */

        // 只读模式：返回成功
        return "{\"success\":true,\"message\":\"本地只读模式，重置操作无效\"}";
    }

    /**
     * 按类型重置数据
     * @param type 数据类型
     * @return 服务器返回的JSON数据
     */
    public static String resetDataByType(String type) {
        /* === 原始API代码 - 已注释，改为只读模式 ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"").append(type).append("\",\"action\":\"reset\"}");
        return sendPostRequest(CHANGE_URL, jsonBuilder.toString());
        === 原始API代码结束 === */

        // 只读模式：返回成功
        return "{\"success\":true,\"message\":\"本地只读模式，重置操作无效\"}";
    }

    /**
     * 发送POST请求到服务器
     * @param urlStr 请求URL
     * @param jsonBody JSON格式的请求体
     * @return 服务器响应内容
     */
    @SuppressWarnings("unused")
    private static String sendPostRequest(String urlStr, String jsonBody) {
        /* === 原始API代码 - 已注释，本地模式不使用网络请求 ===
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置请求方法为POST
            conn.setRequestMethod("POST");
            // 设置请求头
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);  // 允许输出
            conn.setConnectTimeout(10000);  // 连接超时10秒
            conn.setReadTimeout(10000);  // 读取超时10秒

            // 写入请求体
            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            // 获取响应码
            int responseCode = conn.getResponseCode();

            // 根据响应码选择输入流
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            // 读取响应内容
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            // 发生异常时返回错误JSON
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
        === 原始API代码结束 === */

        // 本地模式：不发送网络请求
        return "{\"error\":\"本地模式，网络请求已禁用\"}";
    }
}
