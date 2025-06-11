import axios from 'axios';

// Get API base URL from environment variables (if available)
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

// FraudRule interface definition
export interface FraudRule {
  id?: number;
  name: string;
  description: string;
  ruleType: string;
  thresholdValue?: number;
  suspiciousPattern?: string;
  enabled: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// API service
const apiService = {
  // Get all rules
  getAllRules: async (): Promise<FraudRule[]> => {
    const response = await axios.get(`${API_BASE_URL}/rules`);
    return response.data;
  },

  // Get a single rule by ID
  getRuleById: async (id: number): Promise<FraudRule> => {
    const response = await axios.get(`${API_BASE_URL}/rules/${id}`);
    return response.data;
  },

  // Create a new rule
  createRule: async (rule: FraudRule): Promise<FraudRule> => {
    const response = await axios.post(`${API_BASE_URL}/rules`, rule);
    return response.data;
  },

  // Update an existing rule
  updateRule: async (id: number, rule: FraudRule): Promise<FraudRule> => {
    const response = await axios.put(`${API_BASE_URL}/rules/${id}`, rule);
    return response.data;
  },

  // Delete a rule
  deleteRule: async (id: number): Promise<void> => {
    await axios.delete(`${API_BASE_URL}/rules/${id}`);
  },

  // Enable a rule
  enableRule: async (id: number): Promise<FraudRule> => {
    const response = await axios.patch(`${API_BASE_URL}/rules/${id}/enable`);
    return response.data;
  },

  // Disable a rule
  disableRule: async (id: number): Promise<FraudRule> => {
    const response = await axios.patch(`${API_BASE_URL}/rules/${id}/disable`);
    return response.data;
  },

  // Refresh rules in the engine
  refreshRules: async (): Promise<void> => {
    await axios.post(`${API_BASE_URL}/fraud/rules/refresh`);
  }
};

export default apiService; 