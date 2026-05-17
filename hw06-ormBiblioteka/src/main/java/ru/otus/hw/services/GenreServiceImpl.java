package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
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
    @Transactional
    public GenreDto insert(String name) {
        Genre genre = new Genre(0, name);
        Genre saved = genreRepository.save(genre);
        return genreMapper.toDto(saved);
    }

    @Override
    @Transactional
    public GenreDto update(long id, String name) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        genre.setName(name);
        Genre updated = genreRepository.save(genre);
        return genreMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}