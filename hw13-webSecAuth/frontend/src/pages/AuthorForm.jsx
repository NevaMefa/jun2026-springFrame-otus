import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { apiGet, apiPost, apiPut } from '../api/apiClient';

function AuthorForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [fullName, setFullName] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isEdit) {
      apiGet(`/author/${id}`)
        .then(data => {
          setFullName(data.fullName);
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

    const url = `/author${isEdit ? '/' + id : ''}`;
    const method = isEdit ? apiPut : apiPost;

    method(url, { fullName })
      .then(() => navigate('/authors'))
      .catch(error => console.error('Error:', error));
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>{isEdit ? 'Редактировать автора' : 'Добавить автора'}</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Имя:</label><br />
          <input type="text" value={fullName} onChange={e => setFullName(e.target.value)} required />
        </div>
        <button type="submit">Сохранить</button>
        <Link to="/authors">Отмена</Link>
      </form>
    </div>
  );
}

export default AuthorForm;