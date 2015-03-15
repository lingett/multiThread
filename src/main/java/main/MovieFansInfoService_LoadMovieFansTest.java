package main;

import com.dianping.movie.fans.api.service.MovieFansInfoService;
import common.AbstractPigeonTest;
import entity.FutureResult;
import entity.PigeonServiceConfig;
import lombok.Data;
import main.task.MovieFansInfoService_LoadMovieFansTask;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Data
public class MovieFansInfoService_LoadMovieFansTest extends AbstractPigeonTest<MovieFansInfoService, FutureResult> {
    private String inputFilePath;

    public MovieFansInfoService_LoadMovieFansTest(int poolSize, int cycleTimes, PigeonServiceConfig pigeonServiceConfig) {
        super(poolSize, cycleTimes, pigeonServiceConfig);
    }

    @Override
    protected void generateTask(BlockingQueue<Callable<FutureResult>> taskQueue) {
        try {
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource(inputFilePath);
            BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
            String inputStr = br.readLine();
            while (StringUtils.isNotBlank(inputStr)) {
                MovieFansInfoService_LoadMovieFansTask task = new MovieFansInfoService_LoadMovieFansTask(getService(), Integer.parseInt(inputStr));
                taskQueue.put(task);
                getProducedCount().incrementAndGet();
                inputStr = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        int poolSize = 10;
        int cycleTimes = 100;
        String inputFilePath = "/input/LoadMovieFans_UserIdList.txt";

        PigeonServiceConfig pigeonServiceConfig = new PigeonServiceConfig();
        pigeonServiceConfig.setLocalPort(2001);
        pigeonServiceConfig.setRemotePort(2100);
        pigeonServiceConfig.setServiceName("http://service.dianping.com/movieFans/service/movieFansInfoRemoteService_1.0.0");
        pigeonServiceConfig.setServiceUrl("192.168.223.199");

        MovieFansInfoService_LoadMovieFansTest instance = new MovieFansInfoService_LoadMovieFansTest(poolSize, cycleTimes, pigeonServiceConfig);
        instance.setInputFilePath(inputFilePath);
        instance.execute();
        System.out.println("end");
    }
}
