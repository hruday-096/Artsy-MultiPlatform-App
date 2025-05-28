import { useEffect, useState } from "react";
import {
  getArtistDetails,
  ArtistDetails as ArtistDetailsType,
  Artist,
  getSimilarArtists,
} from "../services/artsy";
import ArtworkGallery from "./ArtworkGallery";
import { Artwork } from "./ArtworkGallery";
import ArtworkModal from "./ArtworkModal";
import { useAuth } from "../context/authContext";
import { FaStar, FaRegStar } from "react-icons/fa";
import ArtistCarousel from "./ArtistCarousel";

type Props = {
  id: string;
  favoriteIds: string[];
  onToggleFavorite: (artist: Artist) => void;
  onSelectArtist: (id: string) => void;
};

const ArtistDetails = ({ id, favoriteIds, onToggleFavorite, onSelectArtist }: Props) => {
  const { isAuthenticated } = useAuth();
  const [details, setDetails] = useState<ArtistDetailsType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [activeTab, setActiveTab] = useState<"info" | "artworks">("info");
  const [selectedArtwork, setSelectedArtwork] = useState<Artwork | null>(null);
  const [showModal, setShowModal] = useState(false);

  const [similarArtists, setSimilarArtists] = useState<Artist[]>([]);
  const [loadingSimilar, setLoadingSimilar] = useState(false);

  const handleArtworkClick = (artwork: Artwork) => {
    setSelectedArtwork(artwork);
    setShowModal(true);
  };
  
  // Handler for artist selection from similar artists
  const handleArtistSelect = (artistId: string) => {
    onSelectArtist(artistId); 
  };

  useEffect(() => {
    const fetchDetails = async () => {
      setLoading(true);
      setError("");
      try {
        const data = await getArtistDetails(id);
        setDetails(data);
      } catch (err) {
        console.error("Artist details error:", err);
        setError("Failed to load artist details.");
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [id]);

  // Add effect to fetch similar artists
  useEffect(() => {
    const fetchSimilarArtists = async () => {
      if (!isAuthenticated) return;
      
      setLoadingSimilar(true);
      try {
        const data = await getSimilarArtists(id);
        setSimilarArtists(data);
      } catch (err) {
        console.error("Similar artists error:", err);
      } finally {
        setLoadingSimilar(false);
      }
    };

    fetchSimilarArtists();
  }, [id, isAuthenticated]);

  if (loading) return <p>Loading artist details...</p>;
  if (error) return <p className="text-danger">{error}</p>;
  if (!details) return null;

  return (
    <div
      className="mt-4"
      style={{
        backgroundColor: "white",
        padding: 0,
        borderRadius: "0.5rem",
      }}
    >
      <ArtworkModal
        show={showModal}
        artwork={selectedArtwork}
        onHide={() => setShowModal(false)}
      />

      <div
        className="d-flex mb-3"
        style={{ borderRadius: "0.5rem", overflow: "hidden" }}
      >
        <button
          className="flex-fill text-center border-0 fw-semibold py-2"
          style={{
            backgroundColor: activeTab === "info" ? "#1D6FC7" : "white",
            color: activeTab === "info" ? "white" : "#1D6FC7",
          }}
          onClick={() => setActiveTab("info")}
        >
          Artist Info
        </button>
        <button
          className="flex-fill text-center border-0 fw-semibold py-2"
          style={{
            backgroundColor: activeTab === "artworks" ? "#1D6FC7" : "white",
            color: activeTab === "artworks" ? "white" : "#1D6FC7",
          }}
          onClick={() => setActiveTab("artworks")}
        >
          Artworks
        </button>
      </div>

      <div >
        {activeTab === "info" && (
          <>
            <div className="d-flex justify-content-center align-items-center gap-2 mb-2">
              <h3 className="mb-0">{details.name}</h3>
              {isAuthenticated && (
                <div
                  style={{ cursor: "pointer" }}
                  onClick={() =>
                    onToggleFavorite({
                      id,
                      name: details.name,
                      thumbnail: null,
                    })
                  }
                >
                  {favoriteIds.includes(id) ? (
                    <FaStar color="gold" size={18} />
                  ) : (
                    <FaRegStar color="black" size={18} />
                  )}
                </div>
              )}
            </div>

            <p className="text-center mb-2">
              {details.nationality}, {details.birthday} - {details.deathday}
            </p>

            {details.biography.split("\n").map((para, idx) => (
              <p key={idx}>{para}</p>
            ))}
            
            {isAuthenticated && (
              <div className="mt-5">
                <h4 className="mb-3">Similar Artists</h4>
                {loadingSimilar ? (
                  <p>Loading similar artists...</p>
                ) : similarArtists.length > 0 ? (
                  <ArtistCarousel
                    artists={similarArtists}
                    onSelect={handleArtistSelect}
                    favoriteIds={favoriteIds}
                    onToggleFavorite={onToggleFavorite}
                    selectedArtistId={null}
                  />
                ) : (
                  <p>No similar artists found.</p>
                )}
              </div>
            )}
          </>
        )}

        {activeTab === "artworks" && (
          <ArtworkGallery artistId={id} onArtworkClick={handleArtworkClick} />
        )}
      </div>
    </div>
  );
};

export default ArtistDetails;