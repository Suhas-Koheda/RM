import axios from 'axios';

const API_URL = '/analyse';

export const analyzeResume = async (resumeFile, jobDescription) => {
  try {
    const formData = new FormData();
    formData.append('resumeFile', resumeFile);
    formData.append('JD', jobDescription);

    const response = await axios.post(`${API_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
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
    const response = await axios.get(`${API_URL}/resume`);
    return response.data;
  } catch (error) {
    console.error('Error fetching resumes:', error);
    throw error;
  }
};