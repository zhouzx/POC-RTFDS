import React, { useEffect, useState } from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper,
  Button,
  IconButton,
  Typography,
  Box,
  Chip,
  Tooltip
} from '@mui/material';
import { 
  Edit as EditIcon, 
  Delete as DeleteIcon,
  ToggleOn as EnableIcon,
  ToggleOff as DisableIcon,
  Refresh as RefreshIcon
} from '@mui/icons-material';
import apiService, { FraudRule } from '../services/api';
import { useNavigate } from 'react-router-dom';

const RuleList: React.FC = () => {
  const [rules, setRules] = useState<FraudRule[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchRules = async () => {
    setLoading(true);
    try {
      const data = await apiService.getAllRules();
      setRules(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching rules:', err);
      setError('Failed to load rules. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRules();
  }, []);

  const handleEdit = (id: number) => {
    navigate(`/edit/${id}`);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this rule?')) {
      try {
        await apiService.deleteRule(id);
        setRules(rules.filter(rule => rule.id !== id));
      } catch (err) {
        console.error('Error deleting rule:', err);
        setError('Failed to delete rule. Please try again later.');
      }
    }
  };

  const handleToggleStatus = async (id: number, currentStatus: boolean) => {
    try {
      let updatedRule: FraudRule;
      if (currentStatus) {
        updatedRule = await apiService.disableRule(id);
      } else {
        updatedRule = await apiService.enableRule(id);
      }

      setRules(rules.map(rule => rule.id === id ? updatedRule : rule));
    } catch (err) {
      console.error('Error toggling rule status:', err);
      setError(`Failed to ${currentStatus ? 'disable' : 'enable'} rule. Please try again later.`);
    }
  };

  const handleRefreshRules = async () => {
    try {
      await apiService.refreshRules();
      alert('Rules refreshed successfully in the fraud detection engine.');
    } catch (err) {
      console.error('Error refreshing rules:', err);
      setError('Failed to refresh rules in the engine. Please try again later.');
    }
  };

  const getRuleTypeChip = (ruleType: string) => {
    let color: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default' = 'default';
    
    switch(ruleType) {
      case 'AMOUNT':
      case 'AMOUNT_THRESHOLD':
        color = 'primary';
        break;
      case 'ACCOUNT_PATTERN':
      case 'SUSPICIOUS_ACCOUNT':
        color = 'warning';
        break;
      case 'FREQUENCY':
        color = 'error';
        break;
      case 'LOCATION':
        color = 'info';
        break;
      case 'DEVICE':
        color = 'secondary';
        break;
      default:
        color = 'default';
    }
    
    return <Chip label={ruleType} color={color} size="small" />;
  };

  if (loading) return <Typography>Loading rules...</Typography>;

  return (
    <Box sx={{ width: '100%', p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Typography variant="h5" component="h1">Fraud Detection Rules</Typography>
        <Box>
          <Button 
            variant="outlined" 
            color="secondary"
            startIcon={<RefreshIcon />}
            onClick={handleRefreshRules}
          >
            Refresh Engine Rules
          </Button>
        </Box>
      </Box>

      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      {rules.length === 0 ? (
        <Typography>No rules found. Create a new rule to get started.</Typography>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Rule Type</TableCell>
                <TableCell>Threshold</TableCell>
                <TableCell>Pattern</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rules.map((rule) => (
                <TableRow key={rule.id}>
                  <TableCell>{rule.id}</TableCell>
                  <TableCell>{rule.name}</TableCell>
                  <TableCell>{rule.description}</TableCell>
                  <TableCell>{getRuleTypeChip(rule.ruleType)}</TableCell>
                  <TableCell>{rule.thresholdValue || '-'}</TableCell>
                  <TableCell>{rule.suspiciousPattern || '-'}</TableCell>
                  <TableCell>
                    <Chip 
                      label={rule.enabled ? 'Enabled' : 'Disabled'} 
                      color={rule.enabled ? 'success' : 'default'} 
                      size="small" 
                    />
                  </TableCell>
                  <TableCell>
                    <Tooltip title="Edit Rule">
                      <IconButton 
                        size="small" 
                        color="primary" 
                        onClick={() => handleEdit(rule.id!)}
                      >
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title={rule.enabled ? 'Disable Rule' : 'Enable Rule'}>
                      <IconButton 
                        size="small" 
                        color={rule.enabled ? 'default' : 'success'} 
                        onClick={() => handleToggleStatus(rule.id!, rule.enabled)}
                      >
                        {rule.enabled ? <DisableIcon /> : <EnableIcon />}
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete Rule">
                      <IconButton 
                        size="small" 
                        color="error" 
                        onClick={() => handleDelete(rule.id!)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
};

export default RuleList; 