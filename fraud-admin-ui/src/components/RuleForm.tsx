import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, Button, TextField, Switch, FormControlLabel, Alert, CircularProgress } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import apiService, { FraudRule } from '../services/api';

const RuleForm = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [rule, setRule] = useState<FraudRule | null>(null);
  const [thresholdValue, setThresholdValue] = useState('');
  const [pattern, setPattern] = useState('');
  const [enabled, setEnabled] = useState(false);
  
  useEffect(() => {
    if (!id) return;
    
    const fetchRule = async () => {
      try {
        const data = await apiService.getRuleById(parseInt(id));
        setRule(data);
        setThresholdValue(data.thresholdValue?.toString() || '');
        setPattern(data.suspiciousPattern || '');
        setEnabled(data.enabled);
      } catch (err) {
        console.error('Error fetching rule:', err);
        setError('Failed to load rule. Please try again later.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchRule();
  }, [id]);
  
  const handleSave = async () => {
    if (!rule || !id) return;
    
    setSaving(true);
    
    try {
      const updatedRule: FraudRule = {
        ...rule,
        thresholdValue: thresholdValue ? parseFloat(thresholdValue) : undefined,
        suspiciousPattern: pattern || undefined,
        enabled
      };
      
      await apiService.updateRule(parseInt(id), updatedRule);
      navigate('/');
    } catch (err) {
      console.error('Error saving rule:', err);
      setError('Failed to save rule. Please try again later.');
    } finally {
      setSaving(false);
    }
  };
  
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
        <CircularProgress />
      </Box>
    );
  }
  
  if (!rule) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">Rule not found</Alert>
      </Box>
    );
  }
  
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Edit Rule Parameters
      </Typography>
      
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      
      <Paper sx={{ p: 3 }}>
        <Box sx={{ mb: 3 }}>
          <Typography variant="subtitle1" fontWeight="bold">Rule Name:</Typography>
          <Typography variant="body1">{rule.name}</Typography>
        </Box>
        
        <Box sx={{ mb: 3 }}>
          <Typography variant="subtitle1" fontWeight="bold">Rule Type:</Typography>
          <Typography variant="body1">{rule.ruleType}</Typography>
        </Box>
        
        <Box sx={{ mb: 3 }}>
          <Typography variant="subtitle1" fontWeight="bold">Description:</Typography>
          <Typography variant="body1">{rule.description}</Typography>
        </Box>
        
        <Typography variant="h6" sx={{ mt: 4, mb: 2 }}>
          Editable Parameters
        </Typography>
        
        <TextField
          label="Threshold Value"
          type="number"
          value={thresholdValue}
          onChange={e => setThresholdValue(e.target.value)}
          fullWidth
          margin="normal"
          InputProps={{ inputProps: { step: 0.01 } }}
        />
        
        <TextField
          label="Pattern (regex)"
          value={pattern}
          onChange={e => setPattern(e.target.value)}
          fullWidth
          margin="normal"
        />
        
        <FormControlLabel
          control={
            <Switch
              checked={enabled}
              onChange={e => setEnabled(e.target.checked)}
              color="primary"
            />
          }
          label="Enable this rule"
          sx={{ mt: 2, mb: 2 }}
        />
        
        <Box sx={{ mt: 4, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
          <Button 
            variant="outlined"
            onClick={() => navigate('/')}
            disabled={saving}
          >
            Cancel
          </Button>
          <Button 
            variant="contained" 
            color="primary"
            onClick={handleSave}
            disabled={saving}
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default RuleForm; 