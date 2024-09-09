package com.solocongee.presentationgen_back_end.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aspose.slides.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Random;

@Slf4j
public class MergePPT {

    static {
        try {
            InputStream is;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                is = new FileInputStream("../data/license.xml");
            } else {
                is = new FileInputStream("/root/data/license.xml");
            }
            License license = new License();
            license.setLicense(is);
            is.close();
        } catch (Exception e) {
            log.error("Error setting Aspose license", e);
        }
    }

    private static WebClient createWebClient() {
        HttpClient httpClient = HttpClient.create();
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(connector)
                .baseUrl("https://pixabay.com/api/")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 设置最大内存缓冲区大小为10MB
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3") // 添加User-Agent
                .build();
    }

    private static void addDelay() {
        Random random = new Random();
        int delay = random.nextInt(3000); // 0到3秒之间的随机延时
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during delay", e);
        }
    }

    public static byte[] mergePPT(ArrayList<Path> paths, String mainObject) {
        long begin = System.currentTimeMillis();

        //创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Future<Presentation>[] futures = new Future[paths.size()];
        AtomicInteger page = new AtomicInteger(1);
        AtomicInteger imageIndex = new AtomicInteger(0);
        log.info("主题物品是{}", mainObject);
        mainObject = mainObject.replace(" ", "+");
        String finalMainObject = mainObject;
        WebClient client = createWebClient();

        //定义Future数组
        for (int i = 0; i < paths.size(); i++) {
            final int index = i;
            futures[i] = executor.submit(() -> {
                log.info("处理文件：{}", paths.get(index).getFileName());
                Presentation pres = new Presentation(Files.newInputStream(paths.get(index)));
                if (paths.get(index).getFileName().toString().contains("有图")) {
                    boolean success = false;
                    int count = 0;
                    while (!success) {
                        try {
                            addDelay(); // 添加延时
                            count++;
                            Mono<String> responseMono = client.get()
                                    .uri(uriBuilder -> uriBuilder.queryParam("per_page", "3")
                                            .queryParam("page", page.get())
                                            .queryParam("q", finalMainObject)
                                            .queryParam("key", "?????????????")
                                            .build())
                                    .retrieve()
                                    .bodyToMono(String.class);

                            String responseJson = responseMono.block();
                            JSONObject jsonObject = JSON.parseObject(responseJson);
                            String imageUrl = jsonObject.getJSONArray("hits").getJSONObject(imageIndex.getAndIncrement()).getString("largeImageURL");

                            if (imageIndex.get() >= 3) {
                                imageIndex.set(0);
                                page.incrementAndGet();
                            }

                            Mono<byte[]> imageMono = client.get()
                                    .uri(imageUrl)
                                    .retrieve()
                                    .bodyToMono(byte[].class)
                                    .doOnSuccess(imageData -> {
                                        IPPImage img = pres.getImages().addImage(new ByteArrayInputStream(imageData));
                                        ISlide slide = pres.getSlides().get_Item(0);
                                        IShape pictureFrame = slide.getShapes().addPictureFrame(ShapeType.Rectangle, 0, 0,
                                                (float) pres.getSlideSize().getSize().getWidth(),
                                                (float) pres.getSlideSize().getSize().getHeight(), img);
                                        slide.getShapes().reorder(0, pictureFrame);
                                        log.info("全屏图片已添加至幻灯片底层");
                                    });
                            // 强制等待操作完成
                            imageMono.block();

                            success = true;
                            //三次重试获取图片
                        } catch (Exception e) {
                            log.error("处理图片时出现错误", e);
                            if (count > 3) {
                                log.error("重试次数已达上限，无法加载图片");
                                IPPImage img = pres.getImages().addImage(new FileInputStream(String.valueOf(Paths.get("/root/data/background.png"))));
                                ISlide slide = pres.getSlides().get_Item(0);
                                IShape pictureFrame = slide.getShapes().addPictureFrame(ShapeType.Rectangle, 0, 0,
                                        (float) pres.getSlideSize().getSize().getWidth(),
                                        (float) pres.getSlideSize().getSize().getHeight(), img);
                                slide.getShapes().reorder(0, pictureFrame);
                                log.info("缺省图片已添加至幻灯片底层");
                                break;
                            }
                        }
                    }
                }
                return pres;
            });
        }

        Presentation finalPres = new Presentation();
        ByteArrayOutputStream baos = null;
        try {
            finalPres.getSlideSize().setSize(1920, 1080, SlideSizeScaleType.EnsureFit);
            for (Future<Presentation> future : futures) {
                try {
                    Presentation pres = future.get();
                    for (ISlide slide : pres.getSlides()) {
                        finalPres.getSlides().addClone(slide);
                    }
                    pres.dispose();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Failed to process file", e);
                }
            }
            if (finalPres.getSlides().size() > 1) {
                finalPres.getSlides().removeAt(0);
            }
            baos = new ByteArrayOutputStream();
            finalPres.save(baos, SaveFormat.Pptx);
        } catch (Exception e) {
            log.error("合并演示文稿失败", e);
        } finally {
            finalPres.dispose();
            executor.shutdownNow();
        }

        long end = System.currentTimeMillis();
        log.info("合并完成，共耗时 " + ((end - begin) / 1000.0) + " 秒。");
        return baos.toByteArray();
    }
}
