const mongoose = require('mongoose');
const userSchema = new mongoose.Schema({
  fullname: String,
  email: { type: String, unique: true },
  password: String,
  profileImageUrl: String,
  favorites: [
    {
      id: { type: String, required: true },
      title: String,
      date: String,
      thumbnail: String,
      birthYear: String,
      deathYear: String,
      nationality: String,
      addedAt: { type: Date, default: Date.now }
    }
  ]
});
module.exports = mongoose.model('User', userSchema);