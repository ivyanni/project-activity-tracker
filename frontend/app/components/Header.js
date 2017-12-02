import React from 'react';
import { NavLink } from 'react-router-dom';

const Header = () => (
    <header>
        <nav className="navbar navbar-expand-lg navbar-dark" style={{backgroundColor: '#4c75a3'}}>
            <div className="collapse navbar-collapse" id="navbarNav">
                <ul className="navbar-nav">
                    <li className="nav-item"><NavLink className='nav-link' exact to='/'>Home</NavLink></li>
                    <li className="nav-item"><NavLink className='nav-link' to='/about'>About</NavLink></li>
                </ul>
            </div>
        </nav>
    </header>
);

export default Header;