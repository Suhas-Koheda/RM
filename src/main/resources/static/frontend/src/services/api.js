import axios from 'axios';

const API_URL = process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : '';
const ANALYSE_URL = API_URL + '/analyse';
const AUTH_URL = API_URL + '/auth';

export const analyzeResume = async (resumeFile, jobDescription, title) => {
  try {
    const formData = new FormData();
    formData.append('resumeFile', resumeFile);
    formData.append('JD', jobDescription);
    formData.append('title', title || resumeFile.name);

    const response = await axios.post(`${ANALYSE_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        ...(localStorage.getItem('accessToken') && { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` })
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error analyzing resume:', error);
    throw error;
  }
};

export const getResumes = async () => {
  try {
    const response = await axios.get(`${ANALYSE_URL}/resume`, {
      headers: {
        ...(localStorage.getItem('accessToken') && { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` })
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching resumes:', error);
    throw error;
  }
};

export const login = async (email, password) => {
  try {
    const response = await axios.post(`${AUTH_URL}/login`, {
      userId: email,
      pwd: password
    });
    if (response.data.accessToken) {
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
    }
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const register = async (email, password) => {
  try {
    const response = await axios.post(`${AUTH_URL}/register`, {
      userId: email,
      pwd: password
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const refresh = async () => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    const response = await axios.post(`${AUTH_URL}/refresh`, refreshToken, {
      headers: { 'Content-Type': 'application/json' }
    });
    if (response.data.accessToken) {
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
    }
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const validateEmail = async (email) => {
  try {
    // Backend expects a JSON string in the body
    const response = await axios.post(`${AUTH_URL}/email`, JSON.stringify(email), {
      headers: { 'Content-Type': 'application/json' }
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const deleteResume = async (id) => {
  try {
    const response = await axios.post(`${ANALYSE_URL}/delete/${id}`, {}, {
      headers: {
        ...(localStorage.getItem('accessToken') && { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` })
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting resume:', error);
    throw error;
  }
};
