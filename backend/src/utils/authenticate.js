const axios = require('axios');
const dotenv = require('dotenv');
dotenv.config();

let tokenInfo = {
  token: null,
  expires_at: 0,
};

async function authenticate() {
  const currentTime = Date.now() / 1000;

  if (!tokenInfo.token || currentTime >= tokenInfo.expires_at) {
    try {
      const response = await axios.post('https://api.artsy.net/api/tokens/xapp_token', {
        client_id: process.env.ARTSY_CLIENT_ID,
        client_secret: process.env.ARTSY_CLIENT_SECRET,
      });


      const data = response.data;
      tokenInfo.token = data.token;
      tokenInfo.expires_at = Math.floor(new Date(data.expires_at).getTime() / 1000);
      return tokenInfo.token;
    } catch (err) {
      console.error('Authentication Error:', err.message);
      throw err;
    }
  }
  return tokenInfo.token;
}

module.exports = authenticate;
