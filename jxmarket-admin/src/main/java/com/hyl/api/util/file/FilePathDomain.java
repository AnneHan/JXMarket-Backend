package com.hyl.api.util.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AnneHan
 * @date 2023-09-15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilePathDomain {

    /**
     * pdf 或 file
     */
    String filePath;

    /**
     * 文件生成目录
     */
    String fileGeneratePath;

    /**
     * 文件预览目录
     */
    String filePreviewPath;

    /**
     * pdf全路径（文件路径+文件名称）
     */
    String fileFullPath;

    /**
     * 图片生成的目录
     */
    String imageGeneratePath;

    /**
     * 图片预览的目录
     */
    String imagePreviewPath;

    /**
     * 文件名称
     */
    String fileName;

    /**
     * 与edoc管理的fileId 或 是文件路径/rider/ServiceManual/xx
     */
    String fileID;
}
