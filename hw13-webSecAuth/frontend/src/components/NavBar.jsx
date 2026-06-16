import { Link, useNavigate } from 'react-router-dom';
import { isAuthenticated, logout, isAdmin } from '../api/apiClient';

function NavBar() {
    const navigate = useNavigate();
    const authenticated = isAuthenticated();
    const username = sessionStorage.getItem('username');
    const admin = isAdmin();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (!authenticated) {
        return null;
    }

    return (
        <nav style={{ padding: '10px 20px', backgroundColor: '#f8f9fa' }}>
            <h2>📚 Библиотека</h2>
            <div style={{ display: 'flex', gap: '15px', alignItems: 'center', flexWrap: 'wrap' }}>
                <Link to="/books">Книги</Link>
                <Link to="/authors">Авторы</Link>
                <Link to="/genres">Жанры</Link>
            </div>
            <div style={{ marginTop: '10px' }}>
                <span>Привет, {username}! </span>
                {admin ? (
                    <span style={{ color: 'green', fontWeight: 'bold' }}>🔑 Администратор</span>
                ) : (
                    <span style={{ color: 'blue' }}>👤 Пользователь</span>
                )}
                <button
                    onClick={handleLogout}
                    style={{ marginLeft: '10px', padding: '5px 10px' }}
                >
                    Выйти
                </button>
            </div>
            <hr />
        </nav>
    );
}

export default NavBar;