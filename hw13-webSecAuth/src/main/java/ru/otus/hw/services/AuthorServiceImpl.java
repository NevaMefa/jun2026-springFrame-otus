package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthorDto> findById(long id) {
        return authorRepository.findById(id).map(authorMapper::toDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AuthorDto insert(CreateAuthorRequestDto requestDto) {
        Author author = new Author(0, requestDto.fullName());
        Author saved = authorRepository.save(author);
        return authorMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AuthorDto update(long id, UpdateAuthorRequestDto requestDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        author.setFullName(requestDto.fullName());
        Author updated = authorRepository.save(author);
        return authorMapper.toDto(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteById(long id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}