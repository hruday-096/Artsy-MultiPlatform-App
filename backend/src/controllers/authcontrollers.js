const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const gravatar = require('gravatar');
const User = require('../models/User');
const crypto = require('crypto');
const JWT_SECRET = process.env.JWT_SECRET;

const register = async (req, res) => {
  const { fullname, email, password } = req.body;
  if (!fullname || !email || !password)
    return res.status(400).json({ message: 'All fields are required!' });
  try {
    const existing = await User.findOne({ email });
    if (existing) return res.status(400).json({ message: 'User with this email already exists.' });
    
    const hashedPassword = await bcrypt.hash(password, 10);
    
    const emailHash = crypto.createHash('sha1').update(email.trim().toLowerCase()).digest('hex');
    const avatar = `https://www.gravatar.com/avatar/${emailHash}?s=200&r=pg&d=identicon`;
    
    console.log('New generated avatar URL:', avatar);
    
    const user = new User({
      fullname,
      email,
      password: hashedPassword,
      profileImageUrl: avatar,
    });
    
    await user.save();
    
    const token = jwt.sign({ id: user._id }, JWT_SECRET, { expiresIn: '1d' });
    res.cookie('token', token, {
      httpOnly: true,
      sameSite: 'None',
      secure: true,
      maxAge: 24 * 60 * 60 * 1000
    });
    
    res.status(201).json({
      message: 'User registered successfully',
      user: {
        fullname: user.fullname,
        email: user.email,
        profileImageUrl: user.profileImageUrl 
      }
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

const login = async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password)
    return res.status(400).json({ message: 'All fields are required' });

  try {
    const user = await User.findOne({ email });
    if (!user || !(await bcrypt.compare(password, user.password)))
      return res.status(400).json({ message: 'Invalid email or password' });

    const token = jwt.sign({ id: user._id }, JWT_SECRET, { expiresIn: '1d' });

    res.cookie('token', token, {
      httpOnly: true,
      sameSite: 'None',
      secure: true,
      maxAge: 24 * 60 * 60 * 1000
    });

    res.json({
      message: 'Login successful',
      user: {
        fullname: user.fullname,
        email: user.email,
        avatar: user.profileImageUrl
      }
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

const logout = (req, res) => {
  res.clearCookie('token', {
    httpOnly: true,
    sameSite: 'None',
    secure: true,
  });
  res.json({ message: 'Logged out successfully' });
};


const getMe = async (req, res) => {
  console.log("[Backend] /api/me route hit");
  console.log("[Backend] Cookies received:", req.cookies);

  const token = req.cookies.token;

  if (!token) {
    console.log("[Backend] No token found in cookies.");
    return res.status(401).json({ error: "Unauthorized" });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    console.log("[Backend] JWT decoded:", decoded);

    const user = await User.findById(decoded.id);

    if (!user) {
      console.log("[Backend] User not found in DB");
      return res.status(404).json({ error: "User not found" });
    }

    console.log("[Backend] User found:", user.email);

    res.json({
      user: {
        fullname: user.fullname,
        email: user.email,
        profileImageUrl: user.profileImageUrl
      }
    });
  } catch (err) {
    console.error("[Backend] JWT decode error:", err.message);
    res.status(401).json({ error: "Invalid or expired token" });
  }
};

module.exports = { getMe };


const deleteAccount = async (req, res) => {
  const token = req.cookies.token;
  if (!token) {
    return res.status(401).json({ error: "Unauthorized" });
  }
  
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    const userId = decoded.id;
    
    const deletedUser = await User.findByIdAndDelete(userId);
    
    if (!deletedUser) {
      return res.status(404).json({ error: "User not found" });
    }
    
    res.clearCookie('token', {
      httpOnly: true,
      sameSite: 'None',
      secure: true,
    });
    
    res.json({ message: 'Account deleted successfully' });
  } catch (err) {
    console.error("Delete account error:", err.message);
    res.status(500).json({ error: "Server error" });
  }
};

module.exports = {
  register,
  login,
  logout,
  getMe,
  deleteAccount
};
