const User = require("../models/User");

exports.getFavorites = async (req, res) => {
  try {
    const user = await User.findById(req.userId);
    if (!user) return res.status(404).json({ error: "User not found" });

    const sortedFavorites = user.favorites.sort(
      (a, b) => new Date(b.addedAt) - new Date(a.addedAt)
    );

    res.json(sortedFavorites);
  } catch (err) {
    res.status(500).json({ error: "Server error" });
  }
};

exports.addFavorite = async (req, res) => {
  const { id, title, date, thumbnail, birthYear, deathYear, nationality } = req.body;
  
  if (!id || !title || !thumbnail) {
    return res.status(400).json({ error: "Missing required fields" });
  }
  
  try {
    const user = await User.findById(req.userId);
    if (!user) return res.status(404).json({ error: "User not found" });
    
    const exists = user.favorites.some(artist => artist.id === id);
    if (exists) return res.status(409).json({ error: "Already in favorites" });
    
    user.favorites.push({
      id,
      title,
      date,
      thumbnail,
      birthYear: birthYear || '',
      deathYear: deathYear || '',
      nationality: nationality || '',
      addedAt: new Date()
    });
    
    await user.save();
    res.json({ message: "Added to favorites" });
  } catch (err) {
    res.status(500).json({ error: "Server error" });
  }
};
exports.removeFavorite = async (req, res) => {
  const { id } = req.params;

  try {
    const user = await User.findById(req.userId);
    if (!user) return res.status(404).json({ error: "User not found" });

    user.favorites = user.favorites.filter(fav => fav.id !== id);
    await user.save();

    res.json({ message: "Removed from favorites" });
  } catch (err) {
    res.status(500).json({ error: "Server error" });
  }
};
