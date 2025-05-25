import axios from 'axios';

const API_URL = '/analyse';
const MODELS_URL = '/models';

export const analyzeResume = async (resumeFile, jobDescription, selectedModel) => {
  try {
    console.log('API service - analyzeResume called with model:', selectedModel);
    
    const formData = new FormData();
    formData.append('resumeFile', resumeFile);
    formData.append('JD', jobDescription);
    
    // Add the selected model if available
    if (selectedModel) {
      console.log('Adding model to FormData:', selectedModel);
      formData.append('model', selectedModel);
    }

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

export const fetchAvailableModels = async () => {
  try {
    const response = await axios.get(MODELS_URL);
    return response.data || [];
  } catch (error) {
    console.error('Error fetching available models:', error);
    // Return a default list in case of failure
    return [
      {
        name: "qwen3:0.6b",
        displayName: "Qwen3 0.6B (Default)",
        family: "qwen3",
        parameterSize: "751.63M"
      }
    ];
  }
};