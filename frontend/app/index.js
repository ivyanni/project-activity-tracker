import React from 'react';
import ReactDOM from 'react-dom';
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css'

import AppRouter from './routes';

ReactDOM.render(<AppRouter />, document.getElementById("app"));