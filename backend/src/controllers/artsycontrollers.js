const axios = require('axios');
const authenticate = require('../utils/authenticate');

const searchArtists = async (req, res) => {
  const query = req.params.query;
  try {
    const token = await authenticate();
    const response = await axios.get(`https://api.artsy.net/api/search?q=${query}&size=10&type=artist`, {
      headers: { 'X-XAPP-Token': token },
    });

    const artists = response.data._embedded.results.map((result) => {
      const artistId = result._links.self.href.split('/').pop();
      let thumbnail = result._links.thumbnail?.href;
      if (thumbnail?.endsWith('missing_image.png')) {
        thumbnail = "/artsy_logo.svg"; 
      }

      return {
        id: artistId,
        name: result.title,
        thumbnail,
      };
    });

    res.json(artists);
  } catch (err) {
    console.error('Search error:', err.message);
    res.status(500).json({ error: 'Failed to fetch search results' });
  }
};

const getArtistDetails = async (req, res) => {
  const artist_id = req.params.id;
  if (!artist_id) return res.status(400).json({ error: 'Artist ID is required' });

  try {
    const token = await authenticate();
    const response = await axios.get(`https://api.artsy.net/api/artists/${artist_id}`, {
      headers: { 'X-XAPP-Token': token },
    });

    const data = response.data;
    const artist = {
      name: data.name || 'Unknown',
      birthday: data.birthday || 'Unknown',
      deathday: data.deathday || 'N/A',
      nationality: data.nationality || 'Unknown',
      biography: data.biography || 'No biography available.',
    };

    res.json(artist);
  } catch (err) {
    console.error('Artist fetch error:', err.response?.status, err.response?.data || err.message);
    res.status(500).json({ error: 'Failed to fetch artist details' });
  }
};

const getArtworks = async (req, res) => {
  const artist_id = req.params.id;
  if (!artist_id) return res.status(400).json({ error: 'Artist ID is required' });

  try {
    const token = await authenticate();
    const response = await axios.get(`https://api.artsy.net/api/artworks?artist_id=${artist_id}&size=10`, {
      headers: { 'X-XAPP-Token': token },
    });

    const artworks = response.data._embedded.artworks.map((result) => {
      let thumbnail = result._links.thumbnail?.href;
      if (thumbnail?.endsWith('missing_image.png')) thumbnail = null;

      return {
        id: result.id,
        title: result.title,
        date: result.date,
        thumbnail,
      };
    });

    res.json(artworks);
  } catch (err) {
    console.error('Artwork fetch error:', err.response?.status, err.response?.data || err.message);
    res.status(err.response?.status || 500).json({
      error: 'Failed to fetch artwork results',
      message: err.response?.data || err.message,
    });
  }
};

const getGenes = async (req, res) => {
  const artwork_id = req.params.id;
  if (!artwork_id) return res.status(400).json({ error: 'Artwork ID is required' });

  try {
    const token = await authenticate();
    const response = await axios.get(`https://api.artsy.net/api/genes?artwork_id=${artwork_id}`, {
      headers: { 'X-XAPP-Token': token },
    });

    const genes = response.data._embedded.genes.map((result) => {
      let thumbnail = result._links.thumbnail?.href;
      if (thumbnail?.endsWith('missing_image.png')) thumbnail = null;

      return {
        category: result.name,
        thumbnail,
      };
    });

    res.json(genes);
  } catch (err) {
    console.error('Gene fetch error:', err.response?.status, err.response?.data || err.message);
    res.status(err.response?.status || 500).json({
      error: 'Failed to fetch gene data',
      message: err.response?.data || err.message,
    });
  }
};

const getSimilarArtists = async (req, res) => {
  const artist_id = req.params.id;
  if (!artist_id) return res.status(400).json({ error: 'Artist ID is required' });

  try {
    const token = await authenticate();
    const response = await axios.get(`https://api.artsy.net/api/artists?similar_to_artist_id=${artist_id}&size=10`, {
      headers: { 'X-XAPP-Token': token },
    });

    const artists = response.data._embedded.artists.map((result) => {
      const artistId = result.id;
      let thumbnail = result._links.thumbnail?.href;
      if (thumbnail?.endsWith('missing_image.png')) {
        thumbnail = "/artsy_logo.svg";
      }

      return {
        id: artistId,
        name: result.name,
        thumbnail,
      };
    });

    res.json(artists);
  } catch (err) {
    console.error('Similar artist fetch error:', err.response?.status, err.response?.data || err.message);
    res.status(500).json({ error: 'Failed to fetch similar artists' });
  }
};


module.exports = {
  searchArtists,
  getArtistDetails,
  getArtworks,
  getGenes,
  getSimilarArtists,
};
