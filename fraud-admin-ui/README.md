# Fraud Admin UI

## Overview
The Fraud Admin UI is a web-based user interface specifically designed for managing fraud detection rules in the fraud detection system. This interface provides a straightforward way to edit, enable/disable, and delete rules that are used by the Fraud Detection Engine to identify potentially fraudulent transactions.

## Features
- Comprehensive rule management interface
  - View all existing fraud detection rules
  - Edit existing rule parameters
  - Enable or disable rules without deleting them
  - Delete rules that are no longer needed
- Rule refresh functionality to update the Fraud Detection Engine
- Simple and intuitive user interface
- Responsive design that works on desktop and tablet devices

## Technical Stack
- React 18+
- TypeScript
- Material-UI for component library
- Axios for API communication
- React Router for navigation

## Prerequisites
- Node.js 16.x or higher
- npm 8.x or higher
- Modern web browser (Chrome, Firefox, Safari, Edge)
- Fraud Detection Engine backend running and accessible

## Configuration
The application is configured through environment variables. Key configuration parameters include:

```
# API Configuration
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_API_RULES_ENDPOINT=/api/rules
REACT_APP_API_REFRESH_ENDPOINT=/api/fraud/rules/refresh

# UI Configuration
REACT_APP_COMPANY_NAME=Your Company
REACT_APP_THEME=light
```

For production deployment, these variables should be set during the build process or through Docker environment variables.

## Building and Running

### Installation
To install dependencies:

```bash
npm install
```

### Development Mode
To run the application in development mode:

```bash
npm start
```

This will start the development server on http://localhost:3000.

### Building for Production
To build the application for production:

```bash
npm run build
```

This will create optimized production files in the `build` directory.

### Running Tests
To run tests:

```bash
npm test
```

### Running with Docker
To run the application using Docker:

```bash
docker build -t fraud-admin-ui .
docker run -p 80:80 fraud-admin-ui
```

### Running with docker-compose
The UI can be run as part of the full system using docker-compose:

```bash
cd /path/to/project/root
docker-compose up fraud-admin-ui
```

## Application Structure

The application is structured as follows:

```
fraud-admin-ui/
├── public/                 # Static files
├── src/
│   ├── services/           # API client for rule management
│   ├── components/         # Reusable UI components
│   │   ├── RuleList.tsx    # Rule listing component
│   │   └── RuleForm.tsx    # Rule editing component
│   ├── App.tsx             # Main application component
│   └── index.tsx           # Application entry point
├── package.json            # Dependencies and scripts
└── tsconfig.json           # TypeScript configuration
```

## Rule Management

### Rule Types
The interface supports management of the following rule types defined in the system:

1. **AMOUNT / AMOUNT_THRESHOLD**: Rules that flag transactions based on their amount value, usually when exceeding a certain threshold
2. **ACCOUNT_PATTERN / SUSPICIOUS_ACCOUNT**: Rules that flag transactions from accounts matching suspicious patterns (using regex patterns)


### Rule Management Workflow

#### Viewing Rules
- The main page displays a table with all configured fraud detection rules
- The table includes columns for rule ID, name, description, rule type, threshold, pattern, status, and actions
- Rules are displayed with color-coded type indicators

#### Editing Rules
1. Click the edit (pencil) icon next to the rule you want to modify
2. Update the rule parameters:
   - Threshold Value: Numeric value (for amount-based rules)
   - Pattern: Regex pattern (for pattern-based rules)
   - Enabled/Disabled status
3. Click "Save Changes" to apply changes

#### Enabling/Disabling Rules
- Click the toggle icon next to a rule to enable or disable it
- Enabled rules have a green status chip, while disabled rules have a gray status chip
- Disabled rules remain in the system but are not applied to transactions

#### Deleting Rules
1. Click the delete (trash) icon next to the rule you want to remove
2. Confirm the deletion in the confirmation dialog
3. The rule will be permanently removed from the system

#### Refreshing Rules
- Click the "Refresh Engine Rules" button at the top of the rule list
- This sends a request to the Fraud Detection Engine to reload all rules
- A confirmation message appears when the refresh is successful

## License
This project is proprietary and confidential.
