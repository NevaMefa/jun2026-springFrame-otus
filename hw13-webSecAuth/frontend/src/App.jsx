import { Navigate, Route, Routes } from 'react-router-dom';
import NavBar from './components/NavBar';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/Login';
import Register from './components/Register';
import AuthorForm from './pages/AuthorForm';
import AuthorList from './pages/AuthorList';
import AuthorView from './pages/AuthorView';
import BookForm from './pages/BookForm';
import BookList from './pages/BookList';
import BookView from './pages/BookView';
import GenreForm from './pages/GenreForm';
import GenreList from './pages/GenreList';
import GenreView from './pages/GenreView';

function App() {
    return (
        <div>
            <NavBar />
            <div style={{ padding: '20px' }}>
                <Routes>
                    {/* Публичные маршруты */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* Защищенные маршруты - доступны всем аутентифицированным */}
                    <Route path="/" element={<ProtectedRoute><Navigate to="/books" replace /></ProtectedRoute>} />
                    <Route path="/books" element={<ProtectedRoute><BookList /></ProtectedRoute>} />
                    <Route path="/books/:id" element={<ProtectedRoute><BookView /></ProtectedRoute>} />
                    <Route path="/authors" element={<ProtectedRoute><AuthorList /></ProtectedRoute>} />
                    <Route path="/authors/:id" element={<ProtectedRoute><AuthorView /></ProtectedRoute>} />
                    <Route path="/genres" element={<ProtectedRoute><GenreList /></ProtectedRoute>} />
                    <Route path="/genres/:id" element={<ProtectedRoute><GenreView /></ProtectedRoute>} />

                    {/* Административные маршруты - только для ADMIN */}
                    <Route path="/books/create" element={<ProtectedRoute requireAdmin><BookForm /></ProtectedRoute>} />
                    <Route path="/books/:id/edit" element={<ProtectedRoute requireAdmin><BookForm /></ProtectedRoute>} />
                    <Route path="/authors/create" element={<ProtectedRoute requireAdmin><AuthorForm /></ProtectedRoute>} />
                    <Route path="/authors/:id/edit" element={<ProtectedRoute requireAdmin><AuthorForm /></ProtectedRoute>} />
                    <Route path="/genres/create" element={<ProtectedRoute requireAdmin><GenreForm /></ProtectedRoute>} />
                    <Route path="/genres/:id/edit" element={<ProtectedRoute requireAdmin><GenreForm /></ProtectedRoute>} />
                </Routes>
            </div>
        </div>
    );
}

export default App;