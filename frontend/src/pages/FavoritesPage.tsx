import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getFavorites, removeFavorite } from "../services/favoritesService";
import { toast } from "react-toastify";

interface Favorite {
  id: string;
  title: string;
  date: string;  
  thumbnail: string;
  birthYear?: string;
  deathYear?: string;
  nationality?: string;
  addedAt: string;
}

const FavoritesPage = () => {
  const [favorites, setFavorites] = useState<Favorite[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const loadFavorites = async () => {
      try {
        const favs = await getFavorites();
        favs.sort((a, b) => new Date(b.addedAt).getTime() - new Date(a.addedAt).getTime());
        setFavorites(favs);
      } catch (err: unknown) {
        if (err instanceof Error) {
          console.error("Failed to load favorites:", err.message);
        } else {
          console.error("Unexpected error loading favorites:", err);
        }
        toast.error("Failed to load favorites");
      } finally {
        setLoading(false);
      }
    };
    
    loadFavorites();
    
    const intervalId = setInterval(() => {
      setFavorites(prev => [...prev]);
    }, 1000);
    
    return () => clearInterval(intervalId);
  }, []);

  const handleRemove = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation(); 
    try {
      await removeFavorite(id);
      setFavorites(favorites.filter(artist => artist.id !== id));
      toast.error("Artist removed from favorites");
    } catch (error) {
      console.error("Failed to remove artist:", error);
      toast.error("Failed to remove artist");
    }
  };

  const navigateToArtist = (id: string) => {
    navigate(`/artist/${id}`);
  };

  const getRelativeTime = (dateString: string) => {
    const now = new Date();
    const addedTime = new Date(dateString);
    const diffInSeconds = Math.floor((now.getTime() - addedTime.getTime()) / 1000);
    
    if (diffInSeconds < 60) {
      return `${diffInSeconds} ${diffInSeconds === 1 ? 'second' : 'seconds'} ago`;
    }
    
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return `${diffInMinutes} ${diffInMinutes === 1 ? 'minute' : 'minutes'} ago`;
    }
    
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return `${diffInHours} ${diffInHours === 1 ? 'hour' : 'hours'} ago`;
    }
    
    const diffInDays = Math.floor(diffInHours / 24);
    return `${diffInDays} ${diffInDays === 1 ? 'day' : 'days'} ago`;
  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <div className="spinner-border text-primary" role="status" />
        <p className="mt-2">Loading favorites...</p>
      </div>
    );
  }

  if (favorites.length === 0) {
    return (
      <div className="d-flex justify-content-center mt-5">
        <div
          className="alert alert-danger text-start w-100"
          role="alert"
          style={{ maxWidth: "80%" }}
        >
          No favorite artists.
        </div>
      </div>
    );
  }
  

  return (
    <div className="container mt-4 mb-5">
      <div className="row g-4">
        {favorites.map((artist) => (
          <div className="col-12 col-md-6 col-lg-4" key={artist.id}>
            <div 
              className="card h-100 text-white border-0"
              onClick={() => navigateToArtist(artist.id)}
              style={{ 
                cursor: 'pointer',
                position: 'relative',
                overflow: 'hidden',
              }}
            >
              <div 
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%',
                  height: '100%',
                  backgroundImage: `url(${artist.thumbnail})`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                  filter: 'blur(8px)',
                  opacity: 0.7,
                }}
              />
              
              <div 
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%',
                  height: '100%',
                  backgroundColor: 'rgba(0, 0, 0, 0.7)',
                }}
              />
              
              <div className="card-body position-relative p-4 d-flex flex-column">
                <h3 className="mb-1">{artist.title}</h3>
                <p className="mb-0 text-white-50">
                  {artist.birthYear || ''} 
                  {artist.deathYear ? ` - ${artist.deathYear}` : ' -'}
                </p>
                <p className="mb-0 text-white-50">{artist.nationality || 'Unknown'}</p>
                <p className="mt-auto mb-0 text-white-50">{getRelativeTime(artist.addedAt)}</p>
                <button 
                  className="btn btn-link text-white p-0 position-absolute"
                  style={{ bottom: '1rem', right: '1rem' }}
                  onClick={(e) => handleRemove(artist.id, e)}
                >
                  Remove
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FavoritesPage;