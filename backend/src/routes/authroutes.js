const express = require('express');
const {
  register,
  login,
  logout,
  getMe,
  deleteAccount,
} = require('../controllers/authcontrollers');

const router = express.Router();

router.post('/register', register);
router.post('/login', login);
router.post('/logout', logout);
router.get('/me', getMe);
router.delete('/delete-account', deleteAccount);

module.exports = router;
