import { useEffect, useState } from "react";
import SearchBar from "../components/SearchBar";
import { addFavorite, getFavorites, removeFavorite } from "../services/favoritesService";
import { toast } from "react-toastify";
import { Artist, getArtistDetails } from "../services/artsy"; 

const Home = () => {
  const [favoriteIds, setFavoriteIds] = useState<string[]>([]);
  
  useEffect(() => {
    const loadFavorites = async () => {
      try {
        const favs = await getFavorites();
        setFavoriteIds(favs.map(f => f.id));
      } catch (error) {
        console.error("Failed to load favorites", error);
      }
    };
    
    loadFavorites();
  }, []);
  
  const handleToggleFavorite = async (artist: Artist) => {
    const isFav = favoriteIds.includes(artist.id);
    try {
      if (isFav) {
        await removeFavorite(artist.id);
        setFavoriteIds(prev => prev.filter(id => id !== artist.id));
        toast.error("Removed from favorites");
      } else {
        try {
          const details = await getArtistDetails(artist.id);
          await addFavorite(artist, details);
          setFavoriteIds(prev => [...prev, artist.id]);
          toast.success("Added to favorites");
        } catch (detailsError) {
          console.error("Failed to fetch artist details:", detailsError);
          await addFavorite(artist);
          setFavoriteIds(prev => [...prev, artist.id]);
          toast.success("Added to favorites (without full details)");
        }
      }
    } catch (error) {
      console.error("Failed to update favorites:", error);
      toast.error("Could not update favorites");
    }
  };
  
  return (
    <div className="container-fluid px-md-5">
      <div className="content-wrapper mx-auto" style={{ maxWidth: "1140px" }}>
        <SearchBar
          favoriteIds={favoriteIds}
          onToggleFavorite={handleToggleFavorite}
        />
      </div>
    </div>
  );
};

export default Home;