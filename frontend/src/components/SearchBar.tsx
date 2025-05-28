import { useState } from "react";
import { searchArtists, Artist } from "../services/artsy";
import ArtistCarousel from "./ArtistCarousel";
import ArtistDetails from "./ArtistDetails";
import { Spinner } from "react-bootstrap";

type Props = {
  onResults?: (artists: Artist[]) => void;
  favoriteIds: string[];
  onToggleFavorite: (artist: Artist) => void;
};

const SearchBar = ({ onResults, favoriteIds, onToggleFavorite }: Props) => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Artist[]>([]);
  const [selectedArtistId, setSelectedArtistId] = useState<string | null>(null); 
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setQuery(e.target.value);
  };

  const handleSearch = async () => {
    if (!query.trim()) return;
    setLoading(true);
    try {
      const artists = await searchArtists(query);
      setResults(artists);
      setSelectedArtistId(null); 
      onResults?.(artists);
    } catch (err) {
      console.error("Search failed:", err);
    } finally {
      setLoading(false);
    }
  };
  
  const handleClear = () => {
    setQuery("");
    setResults([]);
    setSelectedArtistId(null);
    onResults?.([]);
  };

  const handleSelectArtist = (id: string) => {
    setSelectedArtistId(id); 
  };
  
  return (
    <>
      <form
        className="mt-3"
        onSubmit={(e) => {
          e.preventDefault();
          handleSearch();
        }}
      >
        <div className="input-group">
          <input
            type="text"
            className="form-control"
            placeholder="Please enter an artist name."
            value={query}
            onChange={handleInputChange}
            disabled={loading}
          />
          <button
            type="submit"
            className="btn"
            style={{
              backgroundColor: "#205375",
              borderColor: "#205375",
              color: "white",
            }}
            disabled={!query.trim() || loading}
          >
            <>
            Search
              {loading && (
              <Spinner
                animation="border"
                role="status"
                size="sm"
                className="me-2"
                >
                <span className="visually-hidden">Loading...</span>
              </Spinner>
            )}
            </>
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleClear}
            disabled={loading}
          >
            Clear
          </button>
        </div>
      </form>

      <div className="mt-4">
        <ArtistCarousel
          artists={results}
          onSelect={handleSelectArtist}
          favoriteIds={favoriteIds}
          onToggleFavorite={onToggleFavorite}
          selectedArtistId={selectedArtistId}
        />
      </div>

      {selectedArtistId && (
        <ArtistDetails
          id={selectedArtistId}
          favoriteIds={favoriteIds}
          onToggleFavorite={onToggleFavorite}
          onSelectArtist={handleSelectArtist}
        />
      )}
    </>
  );
};

export default SearchBar;