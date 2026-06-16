import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { apiGet, apiPost, apiPut } from '../api/apiClient';

function BookForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [title, setTitle] = useState('');
  const [authorId, setAuthorId] = useState('');
  const [genreIds, setGenreIds] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      apiGet('/author'),
      apiGet('/genre')
    ]).then(([authorsData, genresData]) => {
      setAuthors(authorsData);
      setGenres(genresData);
      setLoading(false);
    }).catch(error => {
      console.error('Error loading data:', error);
      setLoading(false);
    });

    if (isEdit) {
      apiGet(`/book/${id}`)
        .then(data => {
          setTitle(data.title);
          setAuthorId(data.author.id);
          setGenreIds(data.genres.map(g => g.id));
        })
        .catch(error => console.error('Error loading book:', error));
    }
  }, [id, isEdit]);

  const handleGenreChange = (e) => {
    const options = e.target.options;
    const selected = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) {
        selected.push(parseInt(options[i].value));
      }
    }
    setGenreIds(selected);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const bookData = { title, authorId: parseInt(authorId), genreIds };

    const url = `/book${isEdit ? '/' + id : ''}`;
    const method = isEdit ? apiPut : apiPost;

    method(url, bookData)
      .then(() => navigate('/books'))
      .catch(error => console.error('Error saving book:', error));
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>{isEdit ? 'Редактировать книгу' : 'Добавить книгу'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Название:</label><br />
          <input type="text" value={title} onChange={e => setTitle(e.target.value)} required />
        </div>
        <div>
          <label>Автор:</label><br />
          <select value={authorId} onChange={e => setAuthorId(e.target.value)} required>
            <option value="">Выберите автора</option>
            {authors.map(a => <option key={a.id} value={a.id}>{a.fullName}</option>)}
          </select>
        </div>
        <div>
          <label>Жанры (Ctrl+выбор):</label><br />
          <select multiple value={genreIds} onChange={handleGenreChange}>
            {genres.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
          </select>
        </div>
        <button type="submit">Сохранить</button>
        <Link to="/books">Отмена</Link>
      </form>
    </div>
  );
}

export default BookForm;