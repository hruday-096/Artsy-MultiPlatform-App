const express = require('express');
const router = express.Router();
const {
  searchArtists,
  getArtistDetails,
  getArtworks,
  getGenes,
  getSimilarArtists
} = require('../controllers/artsycontrollers');

router.get('/search/:query', searchArtists);
router.get('/artistdetails/:id', getArtistDetails);
router.get('/artworks/:id', getArtworks);
router.get('/genes/:id', getGenes);
router.get('/similar/:id', getSimilarArtists);

module.exports = router;
