import React, { Component } from 'react';
import { Route, HashRouter, Switch } from 'react-router-dom';

import Home from './components/Home';
import About from './components/About';
import Header from './components/Header';
import Footer from './components/Footer';

const Layout = () => (
    <div>
        <Header />
        <Switch>
            <Route path="/" exact component={Home} />
            <Route path="/about" component={About} />
        </Switch>
        <Footer />
    </div>
);

export default () => (
    <div>
        <HashRouter>
            <Route path="*" component={Layout} />
        </HashRouter>
    </div>
);