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