import './App.css';
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";


import NavigationBarClass from "./components/NavigationBar";
import Home from "./components/Home";
import Login from "./components/Login";

function App() {
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBarClass />
                <div className="container-fluid">
                    <Routes>
                        <Route path="home" element={<Home />}/>
                        <Route path="login" element={<Login/>}/>
                    </Routes>
                </div>
            </BrowserRouter>
        </div>
    );
}


export default App;