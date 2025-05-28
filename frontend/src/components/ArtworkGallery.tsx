// import { useEffect, useState } from "react";
// import axios from "axios";

// export type Artwork = {
//   id: string;
//   title: string;
//   date: string;
//   thumbnail: string | null;
// };

// type Props = {
//   artistId: string;
//   onArtworkClick: (artwork: Artwork) => void;
// };

// const ArtworkGallery = ({ artistId, onArtworkClick }: Props) => {
//   const [artworks, setArtworks] = useState<Artwork[]>([]);
//   const [error, setError] = useState("");
//   const [loading, setLoading] = useState(true); // NEW
//   const [hoveredId, setHoveredId] = useState<string | null>(null);

//   useEffect(() => {
//     const fetchArtworks = async () => {
//       setLoading(true); 
//       try {
//         const res = await axios.get(`/api/artworks/${artistId}`);
//         setArtworks(res.data);
//         setError("");
//       } catch (err) {
//         console.error("Artwork fetch error:", err);
//         setError("Failed to load artworks.");
//       } finally {
//         setLoading(false); 
//       }
//     };
//     fetchArtworks();
//   }, [artistId]);

//   if (loading) {
//     return (
//       <div className="text-center my-4">
//         <div className="spinner-border text-primary" role="status" />
//       </div>
//     );
//   }

//   if (error) {
//     return (
//       <div className="alert alert-danger text-center" role="alert">
//         {error}
//       </div>
//     );
//   }

//   if (artworks.length === 0) {
//     return (
//       <div className="alert alert-danger text-center" role="alert">
//         No artworks.
//       </div>
//     );
//   }

//   return (
//     <div className="d-flex flex-row overflow-auto gap-3">
//       {artworks.map((artwork) => (
//         <div
//           key={artwork.id}
//           className="card"
//           style={{ minWidth: "200px" }}
//           onMouseEnter={() => setHoveredId(artwork.id)}
//           onMouseLeave={() => setHoveredId(null)}
//         >
//           <div 
//             style={{ cursor: "pointer" }}
//             onClick={() => onArtworkClick(artwork)}
//           >
//             {artwork.thumbnail ? (
//               <img
//                 src={artwork.thumbnail}
//                 className="card-img-top"
//                 alt={artwork.title}
//                 style={{ height: "160px", objectFit: "cover" }}
//               />
//             ) : (
//               <div className="card-img-top bg-light text-center py-5">
//                 No Image
//               </div>
//             )}
//             <div className="card-body">
//               <h6 className="card-title">{artwork.title}</h6>
//               <p className="card-text text-muted">{artwork.date}</p>
//             </div>
//           </div>
//           <div>
//             <button
//               onClick={() => onArtworkClick(artwork)}
//               style={{ 
//                 width: "100%",
//                 padding: "8px 0",
//                 border: "none",
//                 borderRadius: "0 0 4px 4px", 
//                 cursor: "pointer",
//                 backgroundColor: hoveredId === artwork.id ? "#1D6FC7" : "#f0f0f0",
//                 color: hoveredId === artwork.id ? "white" : "#333",
//                 fontWeight: "500",
//                 transition: "all 0.2s ease",
//                 marginTop: "0" 
//               }}
//             >
//               View Categories
//             </button>
//           </div>
//         </div>
//       ))}
//     </div>
//   );
// };

// export default ArtworkGallery;
import { useEffect, useState } from "react";
import axios from "axios";

export type Artwork = {
  id: string;
  title: string;
  date: string;
  thumbnail: string | null;
};

type Props = {
  artistId: string;
  onArtworkClick: (artwork: Artwork) => void;
};

const ArtworkGallery = ({ artistId, onArtworkClick }: Props) => {
  const [artworks, setArtworks] = useState<Artwork[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [hoveredId, setHoveredId] = useState<string | null>(null);

  useEffect(() => {
    const fetchArtworks = async () => {
      setLoading(true);
      try {
        // Replace only this URL with your actual backend URL
        const res = await axios.get(`https://artsyauth.uc.r.appspot.com/api/artworks/${artistId}`);
        setArtworks(Array.isArray(res.data) ? res.data : []);
        setError("");
      } catch (err) {
        console.error("Artwork fetch error:", err);
        setError("Failed to load artworks.");
      } finally {
        setLoading(false);
      }
    };
    fetchArtworks();
  }, [artistId]);

  if (loading) {
    return (
      <div className="text-center my-4">
        <div className="spinner-border text-primary" role="status" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger text-center" role="alert">
        {error}
      </div>
    );
  }

  if (artworks.length === 0) {
    return (
      <div className="alert alert-danger text-center" role="alert">
        No artworks.
      </div>
    );
  }

  return (
    <div className="d-flex flex-row overflow-auto gap-3">
      {artworks.map((artwork) => (
        <div
          key={artwork.id}
          className="card"
          style={{ minWidth: "200px" }}
          onMouseEnter={() => setHoveredId(artwork.id)}
          onMouseLeave={() => setHoveredId(null)}
        >
          <div 
            style={{ cursor: "pointer" }}
            onClick={() => onArtworkClick(artwork)}
          >
            {artwork.thumbnail ? (
              <img
                src={artwork.thumbnail}
                className="card-img-top"
                alt={artwork.title}
                style={{ height: "160px", objectFit: "cover" }}
              />
            ) : (
              <div className="card-img-top bg-light text-center py-5">
                No Image
              </div>
            )}
            <div className="card-body">
              <h6 className="card-title">{artwork.title}</h6>
              <p className="card-text text-muted">{artwork.date}</p>
            </div>
          </div>
          <div>
            <button
              onClick={() => onArtworkClick(artwork)}
              style={{ 
                width: "100%",
                padding: "8px 0",
                border: "none",
                borderRadius: "0 0 4px 4px", 
                cursor: "pointer",
                backgroundColor: hoveredId === artwork.id ? "#1D6FC7" : "#f0f0f0",
                color: hoveredId === artwork.id ? "white" : "#333",
                fontWeight: "500",
                transition: "all 0.2s ease",
                marginTop: "0" 
              }}
            >
              View Categories
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ArtworkGallery;