package main.task;

import com.dianping.movie.fans.api.dto.MovieFansDto;
import com.dianping.movie.fans.api.service.MovieFansInfoService;
import com.dianping.tuangou.remote.api.deal.protocol.Result;
import entity.CommonTask;

public class MovieFansInfoService_LoadMovieFansTask extends CommonTask<MovieFansInfoService, Integer, Result<MovieFansDto>> {
    public MovieFansInfoService_LoadMovieFansTask(MovieFansInfoService service, Integer param) {
        super(service, param);
    }

    @Override
    protected Result<MovieFansDto> executeCall(MovieFansInfoService service, Integer param) {
        return service.loadMovieFans(param);
    }

    @Override
    protected void handleCallResult(Result<MovieFansDto> movieFansDtoResult) {
        isTrue(movieFansDtoResult.isSuccess(), movieFansDtoResult.getErrorMsg());
    }
}
