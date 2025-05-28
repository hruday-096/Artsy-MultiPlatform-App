const jwt = require("jsonwebtoken");
module.exports = function (req, res, next) {
  const token = req.cookies?.token || req.headers.authorization?.split(" ")[1];
  if (!token) return res.status(401).json({ error: "No token provided" });
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    req.userId = decoded.id; 
    next();
  } catch (err) {
    return res.status(401).json({ error: "Invalid token" });
  }
};