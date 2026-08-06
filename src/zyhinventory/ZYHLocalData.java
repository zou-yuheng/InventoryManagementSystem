package zyhinventory;

import java.io.*;
import java.nio.file.*;

/**
 * 本地数据读取器
 * 从 data/ 目录读取 JSON 数据文件，替代远程 API
 */
public class ZYHLocalData {

    public static String queryData(String type) {
        String fileName;
        switch (type) {
            case "库存":
                fileName = "data/inventory.json";
                break;
            case "入库":
                fileName = "data/inbound.json";
                break;
            case "出库":
                fileName = "data/outbound.json";
                break;
            default:
                return "{\"error\":\"未知数据类型\"}";
        }

        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path), "UTF-8");
            } else {
                return "{\"error\":\"数据文件不存在\"}";
            }
        } catch (Exception e) {
            return "{\"error\":\"读取失败\"}";
        }
    }
}
