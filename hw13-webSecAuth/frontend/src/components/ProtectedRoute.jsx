import { Navigate } from 'react-router-dom';
import { isAuthenticated, isAdmin } from '../api/apiClient';

function ProtectedRoute({ children, requireAdmin = false }) {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }

    if (requireAdmin && !isAdmin()) {
        return <Navigate to="/books" replace />;
    }

    return children;
}

export default ProtectedRoute;