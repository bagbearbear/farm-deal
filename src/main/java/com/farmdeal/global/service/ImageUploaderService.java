package com.farmdeal.global.service;

import com.farmdeal.domain.product.model.Product;
import com.farmdeal.global.external.AwsS3UploadService;
import com.farmdeal.global.img.model.Img;
import com.farmdeal.global.img.repository.ImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageUploaderService {

    private final ImgRepository imgRepository;
    private final AwsS3UploadService awsS3UploadService;

    // 이미지 저장
    public List<String> addImage(List<String> imgPaths, Product product) {

        List<String> imgList = imgPaths.stream()
                .map(imgUrl -> {
                    Img img = new Img(imgUrl, product);
                    imgRepository.save(img);
                    return img.getImageUrl();
                })
                .collect(Collectors.toList());

        return imgList;
    }

    // 이미지 리스트 업데이트
    public List<String> updateImage(List<String> imgPaths, Product product) {
        //저장된 이미지 리스트 가져오기
        List<String> imgList = getListImage(product);
        if (imgPaths != null) {
            // 이미지 삭제
            deleteImageList(product, imgList);
        }
        imgList = addImage(imgPaths, product);
        product.saveImage(imgList.get(0));
        return imgList;
    }
    //이미지 리스트 가져오기
    public List<String> getListImage(Product product) {
        List<String> imgList = imgRepository.findByProduct_Id(product.getId()).stream()
                .map(Img::getImageUrl)
                .collect(Collectors.toList());
        return imgList;
    }
    // 이미지 삭제
    public void deleteImageList(Product product, List<String> imgList) {
            // aws s3에서 이미지 삭제
            imgList.stream()
                    .map(AwsS3UploadService::getFileNameFromURL)
                    .forEach(awsS3UploadService::deleteFile);
            // imgRepository에서 post에 해당하는 이미지 삭제
            imgRepository.deleteByProduct_Id(product.getId());
    }

}
