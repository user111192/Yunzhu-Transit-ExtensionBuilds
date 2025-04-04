package top.xfunny.mod.client.util;

import org.mtr.mapping.holder.NativeImage;

import java.io.File;
import java.io.IOException;

public class ImageGenerator {


    public static void saveNativeImageAsPng(NativeImage image, String outputPath) {
        try {
            // 创建输出文件对象
            File file = new File(outputPath);

            // 调用 NativeImage 的 writeToFile 方法，将图片保存为 PNG 文件
            image.writeTo(file);

            System.out.println("PNG 图像已成功保存到: " + outputPath);
        } catch (IOException e) {
            System.err.println("保存 PNG 文件时出错: " + e.getMessage());
        } finally {
            // 释放 NativeImage 占用的内存
            image.close();
        }
    }

}
