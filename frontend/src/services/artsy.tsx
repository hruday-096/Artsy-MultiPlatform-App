// import axios from "axios";
import API from "./api.tsx";

export interface Artist {
  id: string;
  name: string;
  thumbnail: string | null;
}

export interface ArtistDetails {
  name: string;
  birthday: string;
  deathday: string;
  nationality: string;
  biography: string;
}

export const searchArtists = async (query: string): Promise<Artist[]> => {
  try {
    const response = await API.get(`/api/search/${query}`);
    return response.data;
  } catch (error) {
    console.error("Search API error:", error);
    return [];
  }
};

export const getArtistDetails = async (id: string): Promise<ArtistDetails> => {
  const response = await API.get(`/api/artistdetails/${id}`);
  return response.data;
};

export type Gene = {
  category: string;
  thumbnail: string | null;
};

export const getArtworkGenes = async (artworkId: string): Promise<Gene[]> => {
  const res = await API.get(`/api/genes/${artworkId}`);
  return res.data;
};

export const getSimilarArtists = async (artistId: string): Promise<Artist[]> => {
  try {
    const response = await API.get(`/api/similar/${artistId}`);
    return response.data;
  } catch (error) {
    console.error("Similar artists API error:", error);
    return [];
  }
};