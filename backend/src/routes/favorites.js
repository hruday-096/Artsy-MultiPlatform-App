const express = require("express");
const router = express.Router();
const favoritesController = require("../controllers/favoritesController");
const authenticated = require("../middleware/auth");

console.log("[Backend] favorites.js route file loaded");

router.get("/ping", (req, res) => {
  res.send("Favorites route is working");
});

router.post(
  "/",
  authenticated,
  (req, res, next) => {
    console.log("[Backend] /api/favorites POST hit");
    next();
  },
  favoritesController.addFavorite
);

router.get("/", authenticated, favoritesController.getFavorites);

router.delete("/:id", authenticated, favoritesController.removeFavorite);

module.exports = router;
