import { useEffect, useState } from "react";
import { Modal, Spinner } from "react-bootstrap";
import { Artwork } from "./ArtworkGallery";
import { Gene, getArtworkGenes } from "../services/artsy";

type Props = {
  show: boolean;
  artwork: Artwork | null;
  onHide: () => void;
};

const ArtworkModal = ({ show, artwork, onHide }: Props) => {
  const [genes, setGenes] = useState<Gene[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!artwork) return;
    const fetchGenes = async () => {
      setLoading(true);
      try {
        const data = await getArtworkGenes(artwork.id);
        setGenes(data);
      } catch (err) {
        console.error("Error fetching genes:", err);
        setGenes([]);
      } finally {
        setLoading(false);
      }
    };
    fetchGenes();
  }, [artwork]);

  if (!artwork) return null;

  return (
    <Modal show={show} onHide={onHide} size="lg" centered>
      <Modal.Header closeButton style={{ border: 'none', paddingBottom: 0 }}>
        <div>
          <Modal.Title>{artwork.title}</Modal.Title>
          <p className="text-muted mb-0">{artwork.date}</p>
        </div>
      </Modal.Header>
      <Modal.Body>
        <div className="container-fluid px-3 py-4">
          {loading ? (
            <div className="text-center py-5">
              <Spinner animation="border" />
            </div>
          ) : genes.length === 0 ? (
            <p>No gene data available.</p>
          ) : (
            <div className="row row-cols-2 row-cols-md-4 g-4">
              {genes.map((gene, idx) => (
                <div key={idx} className="col">
                  <div className="card h-100 border" style={{ borderRadius: '0' }}>
                    <div style={{ 
                      height: '200px', 
                      overflow: 'hidden',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      backgroundColor: '#f8f9fa'
                    }}>
                      {gene.thumbnail ? (
                        <img
                          src={gene.thumbnail}
                          alt={gene.category}
                          style={{ 
                            width: '100%', 
                            height: '100%', 
                            objectFit: 'cover' 
                          }}
                        />
                      ) : (
                        <div className="text-center text-muted">No Image</div>
                      )}
                    </div>
                    <div className="card-body p-3 text-center">
                      <p className="card-text mb-0">{gene.category}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </Modal.Body>
    </Modal>
  );
};

export default ArtworkModal;