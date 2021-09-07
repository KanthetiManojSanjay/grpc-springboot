package com.grpcflix.aggregator.service;

import com.grpcflix.aggregator.dto.RecommendedMovie;
import com.grpcflix.aggregator.dto.UserGenre;
import com.learning.grpcflix.common.Genre;
import com.learning.grpcflix.movie.MovieSearchRequest;
import com.learning.grpcflix.movie.MovieSearchResponse;
import com.learning.grpcflix.movie.MovieServiceGrpc;
import com.learning.grpcflix.user.UserGenreUpdateRequest;
import com.learning.grpcflix.user.UserResponse;
import com.learning.grpcflix.user.UserSearchRequest;
import com.learning.grpcflix.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMovieService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @GrpcClient("movie-service")
    private MovieServiceGrpc.MovieServiceBlockingStub movieStub;

    public List<RecommendedMovie> getUserMovieSuggestions(String loginId) {
        UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder().setLoginId(loginId).build();
        UserResponse userResponse = this.userStub.getUserGenre(userSearchRequest);
        MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder().setGenre(userResponse.getGenre()).build();
        MovieSearchResponse movieSearchResponse = this.movieStub.getMovies(movieSearchRequest);
        return movieSearchResponse.getMovieList()
                .stream()
                .map(movie -> new RecommendedMovie(movie.getTitle(), movie.getYear(), movie.getRating()))
                .collect(Collectors.toList());
    }

    public void setUserGenre(UserGenre userGenre) {
        UserGenreUpdateRequest userGenreUpdateRequest = UserGenreUpdateRequest.newBuilder().setLoginId(userGenre.getLoginId())
                .setGenre(Genre.valueOf(userGenre.getGenre().toUpperCase())).build();
        UserResponse userResponse = this.userStub.updateUserGenre(userGenreUpdateRequest);
    }
}
