import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container, 
  Paper, 
  Typography, 
  Button, 
  TextField, 
  Box, 
  CircularProgress,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Tooltip,
  IconButton
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import RefreshIcon from '@mui/icons-material/Refresh';
import { styled } from '@mui/material/styles';
import { analyzeResume, fetchAvailableModels } from '../services/api';

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

function HomePage() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fileName, setFileName] = useState('No file selected');
  const [models, setModels] = useState([]);
  const [selectedModel, setSelectedModel] = useState('');
  const [loadingModels, setLoadingModels] = useState(false);

  // Function to fetch available models
  const fetchModels = async () => {
    setLoadingModels(true);
    try {
      const modelsData = await fetchAvailableModels();
      setModels(modelsData);
      if (modelsData && modelsData.length > 0) {
        // If user hasn't selected a model yet or their selected model isn't available,
        // default to the first available model
        if (!selectedModel || !modelsData.find(model => model.name === selectedModel)) {
          setSelectedModel(modelsData[0].name);
        }
      }
    } catch (error) {
      console.error('Failed to fetch models:', error);
      setError('Failed to fetch available models.');
    } finally {
      setLoadingModels(false);
    }
  };

  // Fetch available models when component mounts
  useEffect(() => {
    fetchModels();
  }, []);

  const handleFileChange = (event) => {
    const selectedFile = event.target.files[0];
    if (selectedFile) {
      if (selectedFile.type === 'application/pdf' || 
          selectedFile.type === 'text/plain') {
        setFile(selectedFile);
        setFileName(selectedFile.name);
        setError('');
      } else {
        setFile(null);
        setFileName('No file selected');
        setError('Please upload a PDF or text file');
      }
    }
  };

  const handleJobDescriptionChange = (event) => {
    setJobDescription(event.target.value);
  };

  const handleModelChange = (event) => {
    setSelectedModel(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!file) {
      setError('Please upload a resume file');
      return;
    }
    if (!jobDescription.trim()) {
      setError('Please enter a job description');
      return;
    }
    if (!selectedModel) {
      setError('Please select an AI model');
      return;
    }

    try {
      setLoading(true);
      setError('');
      console.log('Submitting with model:', selectedModel);
      const result = await analyzeResume(file, jobDescription, selectedModel);
      navigate('/results', { state: { result, jobDescription, fileName: file.name } });
    } catch (error) {
      console.error('Error analyzing resume:', error);
      if (error.response && error.response.status === 500) {
        setError('Server error. The Ollama server might be unreachable. Please try again later.');
      } else {
        setError('Failed to analyze resume. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom align="center">
          Resume Matcher
        </Typography>
        <Typography variant="body1" paragraph align="center">
          Upload your resume and enter a job description to see how well they match.
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Box sx={{ mb: 3 }}>
            <Grid container spacing={2} alignItems="center">
              <Grid item>
                <Button
                  component="label"
                  variant="contained"
                  startIcon={<CloudUploadIcon />}
                >
                  Upload Resume
                  <VisuallyHiddenInput type="file" onChange={handleFileChange} />
                </Button>
              </Grid>
              <Grid item xs>
                <Typography variant="body2">
                  {fileName}
                </Typography>
                <Typography variant="caption" display="block">
                  Accepted formats: PDF, txt
                </Typography>
              </Grid>
            </Grid>
          </Box>

          <TextField
            fullWidth
            label="Job Description"
            multiline
            rows={6}
            value={jobDescription}
            onChange={handleJobDescriptionChange}
            sx={{ mb: 3 }}
            placeholder="Paste the job description here..."
          />
          
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
            <FormControl fullWidth>
              <InputLabel id="model-select-label">AI Model</InputLabel>
              <Select
                labelId="model-select-label"
                id="model-select"
                value={selectedModel}
                onChange={handleModelChange}
                label="AI Model"
                disabled={loadingModels || models.length === 0}
              >
                {loadingModels ? (
                  <MenuItem value="">
                    <em>Loading models...</em>
                  </MenuItem>
                ) : (
                  models.map((model) => (
                    <MenuItem key={model.name} value={model.name}>
                      {model.displayName || model.name} 
                      {model.family && model.parameterSize && ` (${model.family} - ${model.parameterSize})`}
                    </MenuItem>
                  ))
                )}
                {!loadingModels && models.length === 0 && (
                  <MenuItem value="">
                    <em>No models available</em>
                  </MenuItem>
                )}
              </Select>
            </FormControl>
            <Tooltip title="Refresh available models">
              <IconButton 
                color="primary" 
                onClick={fetchModels} 
                disabled={loadingModels}
                sx={{ ml: 1 }}
              >
                {loadingModels ? <CircularProgress size={24} /> : <RefreshIcon />}
              </IconButton>
            </Tooltip>
          </Box>

          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            disabled={loading || !selectedModel}
            sx={{ py: 1.5 }}
          >
            {loading ? (
              <CircularProgress size={24} color="inherit" />
            ) : (
              'Analyze Match'
            )}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}

export default HomePage;