import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { apiGet, apiPost, apiPut } from '../api/apiClient';

function GenreForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isEdit) {
      apiGet(`/genre/${id}`)
        .then(data => {
          setName(data.name);
          setLoading(false);
        })
        .catch(error => {
          console.error('Error:', error);
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, [id, isEdit]);

  const handleSubmit = (e) => {
    e.preventDefault();

    const url = `/genre${isEdit ? '/' + id : ''}`;
    const method = isEdit ? apiPut : apiPost;

    method(url, { name })
      .then(() => navigate('/genres'))
      .catch(error => console.error('Error:', error));
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>{isEdit ? 'Редактировать жанр' : 'Добавить жанр'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Название:</label><br />
          <input type="text" value={name} onChange={e => setName(e.target.value)} required />
        </div>
        <button type="submit">Сохранить</button>
        <Link to="/genres">Отмена</Link>
      </form>
    </div>
  );
}

export default GenreForm;