package com.movierecommender.backend.movies.moviesearch;

import com.movierecommender.backend.movies.movie.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/movie/search")
public class MovieSearchController {
    private final MovieSearchService movieSearchService;

    @Autowired
    MovieSearchController(MovieSearchService movieSearchService){
        this.movieSearchService = movieSearchService;
    }

    @GetMapping("/{movieName}")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @ResponseStatus(code = HttpStatus.OK, reason = "READ")
    public List<Movie> searchMovieByTag(@PathVariable String movieName){
        System.out.println(movieName);
        return movieSearchService.getMovieByTag(movieName);
    }
}