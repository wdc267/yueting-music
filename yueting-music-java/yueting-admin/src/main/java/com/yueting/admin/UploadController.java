package com.yueting.admin;
import com.yueting.entity.vo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;
@RestController
@RequestMapping("/upload")
public class UploadController {
  @Value("${project.folder:./}")
  private String projectFolder;
  @PostMapping("/image")
  public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
      String dir = projectFolder + "upload/images/";
      File dirFile = new File(dir);
      if (!dirFile.exists()) dirFile.mkdirs();
      String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
      String filename = UUID.randomUUID().toString().replace("-", "") + ext;
      file.transferTo(new File(dir + filename));
      return Result.success("/upload/images/" + filename);
    } catch (Exception e) {
      return Result.error(500, "\u4e0a\u4f20\u5931\u8d25\uff1a" + e.getMessage());
    }
  }
}