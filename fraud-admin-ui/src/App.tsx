import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Container, 
  CssBaseline,
  Box,
  IconButton,
  Tooltip
} from '@mui/material';
import { 
  Dashboard as DashboardIcon,
  Security as SecurityIcon
} from '@mui/icons-material';
import RuleList from './components/RuleList';
import RuleForm from './components/RuleForm';

function App() {
  return (
    <Router>
      <CssBaseline />
      <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
        <AppBar position="static">
          <Toolbar>
            <SecurityIcon sx={{ mr: 2 }} />
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              Fraud Detection Admin
            </Typography>
            <Tooltip title="Dashboard">
              <IconButton color="inherit" component={Link} to="/">
                <DashboardIcon />
              </IconButton>
            </Tooltip>
          </Toolbar>
        </AppBar>
        <Container component="main" sx={{ mt: 4, mb: 4, flexGrow: 1 }}>
          <Routes>
            <Route path="/" element={<RuleList />} />
            <Route path="/edit/:id" element={<RuleForm />} />
          </Routes>
        </Container>
        <Box component="footer" sx={{ py: 3, px: 2, mt: 'auto', backgroundColor: 'background.paper' }}>
          <Container maxWidth="sm">
            <Typography variant="body2" color="text.secondary" align="center">
              Fraud Detection Admin UI &copy; {new Date().getFullYear()}
            </Typography>
          </Container>
        </Box>
      </Box>
    </Router>
  );
}

export default App;
