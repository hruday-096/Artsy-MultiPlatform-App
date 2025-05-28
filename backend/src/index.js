const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
const mongoose = require('mongoose');
const axios = require('axios');
const cookieParser = require('cookie-parser');
const artsyRoutes = require('./routes/artsyroutes');
const authRoutes = require('./routes/authroutes');
dotenv.config();
const app = express();
const port = process.env.PORT || 8080;

app.use(cors({
  origin: 'https://react-frontend-1087721607794.us-central1.run.app',
  credentials: true
}));
app.use(express.json());
app.use(cookieParser());

app.get('/', (req, res) => {
  res.json({ message: 'Hello world!' });
});

app.get("/api", (req, res) => {
  res.json("Proxy test successful");
});

app.use('/api', artsyRoutes);
app.use('/api/auth', authRoutes);
app.use("/api/favorites", require("./routes/favorites"));

app.use((req, res) => {
  console.log(`[Backend] No route matched for ${req.method} ${req.originalUrl}`);
  res.status(404).json({ error: "Route not found" }); 
});

mongoose.connect(process.env.MONGO_URI)
  .then(() => {
    app.listen(port, () => {
      console.log(`Server running at http://localhost:${port}`);
    });
    console.log('MongoDB connected successfully');
  })
  .catch((err) => {
    console.error('MongoDB connection failed:', err.message);
  });
