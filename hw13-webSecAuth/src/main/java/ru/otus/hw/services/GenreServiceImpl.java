package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenreDto> findById(long id) {
        return genreRepository.findById(id).map(genreMapper::toDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public GenreDto insert(CreateGenreRequestDto requestDto) {
        Genre genre = new Genre(0, requestDto.name());
        Genre saved = genreRepository.save(genre);
        return genreMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public GenreDto update(long id, UpdateGenreRequestDto requestDto) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));
        genre.setName(requestDto.name());
        Genre updated = genreRepository.save(genre);
        return genreMapper.toDto(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteById(long id) {
        if (!genreRepository.existsById(id)) {
            throw new EntityNotFoundException("Genre not found with id: " + id);
        }
        genreRepository.deleteById(id);
    }
}