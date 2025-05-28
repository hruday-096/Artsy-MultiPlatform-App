// import axios from "axios";
import API from "./api.tsx";
import { Artist, ArtistDetails } from "./artsy";

export interface Favorite extends Omit<Artist, "name"> {
  title: string;
  date: string;
  thumbnail: string;
  birthYear?: string;
  deathYear?: string;
  nationality?: string;
  addedAt: string;
}

export const getFavorites = async (): Promise<Favorite[]> => {
  const res = await API.get("/api/favorites", { withCredentials: true });
  return res.data;
};

export const addFavorite = async (artist: Artist, details?: ArtistDetails) => {
  const missingImage = "/assets/shared/missing_image.png";
  const fallbackThumbnail = "/assets/artsy-logo.svg"; // relative to public base URL
  const thumbnail = artist.thumbnail === missingImage || !artist.thumbnail 
    ? fallbackThumbnail 
    : artist.thumbnail;
  
  await API.post(
    "/api/favorites",
    {
      id: artist.id,
      title: artist.name,
      date: "unknown",
      thumbnail,
      birthYear: details?.birthday ? details.birthday.split('-')[0] : '',
      deathYear: details?.deathday ? details.deathday.split('-')[0] : '',
      nationality: details?.nationality || ''
    },
    { withCredentials: true }
  );
};

export const removeFavorite = async (id: string) => {
  await API.delete(`/api/favorites/${id}`, { withCredentials: true });
};