import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { apiGet, apiDelete, apiPost, isAdmin } from '../api/apiClient';

function BookView() {
    const { id } = useParams();
    const [book, setBook] = useState(null);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadData();
    }, [id]);

    const loadData = () => {
        setLoading(true);
        apiGet(`/book/${id}`)
            .then(data => {
                setBook(data);
                return apiGet(`/comment?bookId=${id}`);
            })
            .then(data => {
                setComments(data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
                setError('Error loading data');
                setLoading(false);
            });
    };

    const handleDeleteComment = (commentId) => {
        if (!isAdmin()) {
            alert('Только администратор может удалять комментарии');
            return;
        }
        if (confirm('Удалить комментарий?')) {
            apiDelete(`/comment/${commentId}`)
                .then(() => apiGet(`/comment?bookId=${id}`))
                .then(data => setComments(data))
                .catch(error => console.error('Error:', error));
        }
    };

    const handleAddComment = (e) => {
        e.preventDefault();
        if (!isAdmin()) {
            alert('Только администратор может добавлять комментарии');
            return;
        }
        const text = e.target.comment.value;
        if (!text.trim()) return;

        apiPost('/comment', { text, bookId: parseInt(id) })
            .then(() => apiGet(`/comment?bookId=${id}`))
            .then(data => {
                setComments(data);
                e.target.reset();
            })
            .catch(error => console.error('Error:', error));
    };

    if (loading) return <div>Загрузка...</div>;
    if (error) return <div style={{ color: 'red' }}>{error}</div>;
    if (!book) return <div>Книга не найдена</div>;

    return (
        <div>
            <h2>{book.title}</h2>
            <p><strong>Автор:</strong> {book.author.fullName}</p>
            <p><strong>Жанры:</strong> {book.genres.map(g => g.name).join(', ')}</p>

            <Link to="/books">Назад к списку</Link>
            {isAdmin() && (
                <> | <Link to={`/books/${id}/edit`}>Редактировать</Link></>
            )}

            <hr />
            <h3>Комментарии</h3>

            {isAdmin() && (
                <form onSubmit={handleAddComment}>
                    <textarea name="comment" rows="3" cols="50" placeholder="Ваш комментарий..."></textarea>
                    <br />
                    <button type="submit">Добавить комментарий</button>
                </form>
            )}

            {comments.length === 0 && <p>Нет комментариев</p>}
            {comments.map(comment => (
                <div key={comment.id}>
                    <p>{comment.text}</p>
                    {isAdmin() && (
                        <button onClick={() => handleDeleteComment(comment.id)}>Удалить</button>
                    )}
                    <hr />
                </div>
            ))}
        </div>
    );
}

export default BookView;