import React from "react";
import ArtistCard from "./ArtistCard";

type Artist = {
  id: string;
  name: string;
  thumbnail: string | null;
};

type Props = {
  artists: Artist[];
  onSelect: (id: string) => void;
  favoriteIds: string[];
  onToggleFavorite: (artist: Artist) => void;
  selectedArtistId?: string | null; 
};

const ArtistCarousel: React.FC<Props> = ({ 
  artists, 
  onSelect, 
  favoriteIds, 
  onToggleFavorite,
  selectedArtistId 
}) => {
  if (artists.length === 0) return null;

  const getImageUrl = (artist: Artist): string => {
    return artist.thumbnail && artist.thumbnail.trim() !== ""
      ? artist.thumbnail
      : "https://dummyimage.com/180x160/000000/ffffff.png&text=No+Image";
  };

  return (
    <div className="d-flex flex-row overflow-auto py-3">
      {artists.map((artist) => (
        <ArtistCard
          key={artist.id}
          id={artist.id}
          name={artist.name}
          imageUrl={getImageUrl(artist)}
          isFavorited={favoriteIds.includes(artist.id)}
          onClick={() => onSelect(artist.id)}
          onToggleFavorite={() => onToggleFavorite(artist)}
          selected={artist.id === selectedArtistId} 
        />
      ))}
    </div>
  );
};

export default ArtistCarousel;