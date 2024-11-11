import axios from 'axios'

const axiosInstance = axios.create({
    headers: {
        'Content-Type': 'application/json'
    }
});

axiosInstance.interceptors.request.use(
    (config) => {
        const username = process.env.REACT_APP_API_USERNAME;
        const password = process.env.REACT_APP_API_PASSWORD;
        console.log("Username:", username);  // Should log 'johndoe'
        console.log("Password:", password);  // Should log '123'

        if (username && password) {
            config.auth = {
                username: username,
                password: password
            };
        } else {
            console.warn("Username or password is missing in the environment variables.");
        }
        
        return config;
    },
    (error) => Promise.reject(error)
);

export default axiosInstance