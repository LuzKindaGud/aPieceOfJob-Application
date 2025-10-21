import React, { useState } from 'react';
import './pages-style/Alter-jobs.css';
import Jobs from './data/alterjobs.json';
import Altercard from '../components/alter-jobs-card.jsx';

import img from '/src/assets/image/m.png'

function AlterJobs() {
    const handleSelect = (id) => {
        console.log("Job selected:", id);
    }

    const item = Jobs[4];
    if (!item) return null;

    // confirmed = click-selected, hover = temporary preview
    const [confirmedJob, setConfirmedJob] = useState(item);
    const [hoverJob, setHoverJob] = useState(null);

    const displayedJob = hoverJob ?? confirmedJob;

    const handleHover = (index) => {
        if (Jobs?.[index]) setHoverJob(Jobs[index]);
    };
    const handleLeave = () => setHoverJob(null);
    const handleClick = (index) => {
        if (Jobs?.[index]) setConfirmedJob(Jobs[index]);
    };

    return (
        <div>
            <div className="alter-jobs-container">
                <div className="alter-jobs-row">
                    <div className="alter-jobs-image-content left-image">
                        <div className="overlay-box first">
                            <div className="image-wrapper">
                                <img
                                    src={img}
                                    alt="Left Job Banner"
                                    className="alter-jobs-side-banner"
                                    onMouseEnter={() => handleHover(1)}
                                    onFocus={() => handleHover(1)}
                                    onMouseLeave={handleLeave}
                                    onBlur={handleLeave}
                                    onClick={() => handleClick(1)}
                                    role="button"
                                    tabIndex={0}
                                    aria-label="Preview job 2"
                                />
                            </div>
                            <div className="image-wrapper">
                                <img
                                    src={img}
                                    alt="Left Job Banner 2"
                                    className="alter-jobs-side-banner"
                                    onMouseEnter={() => handleHover(2)}
                                    onFocus={() => handleHover(2)}
                                    onMouseLeave={handleLeave}
                                    onBlur={handleLeave}
                                    onClick={() => handleClick(2)}
                                    role="button"
                                    tabIndex={0}
                                    aria-label="Preview job 3"
                                />
                            </div>
                        </div>
                    </div>

                    <Altercard
                        id={displayedJob.id}
                        title={displayedJob.jobsTitle ?? displayedJob.title}
                        img={displayedJob.jobsImg ?? displayedJob.img}
                        onSelect={handleSelect}
                        className="alter-jobs-card"
                    />

                    <div className="alter-jobs-image-content right-image">
                        <div className="image-wrapper">
                            <img
                                src={img}
                                alt="Right Job Banner"
                                className="alter-jobs-side-banner"
                                onMouseEnter={() => handleHover(3)}
                                onFocus={() => handleHover(3)}
                                onMouseLeave={handleLeave}
                                onBlur={handleLeave}
                                onClick={() => handleClick(3)}
                                role="button"
                                tabIndex={0}
                                aria-label="Preview job 4"
                            />
                        </div>
                        <div className="image-wrapper">
                            <img
                                src={img}
                                alt="Right Job Banner 2"
                                className="alter-jobs-side-banner"
                                onMouseEnter={() => handleHover(0)}
                                onFocus={() => handleHover(0)}
                                onMouseLeave={handleLeave}
                                onBlur={handleLeave}
                                onClick={() => handleClick(0)}
                                role="button"
                                tabIndex={0}
                                aria-label="Preview job 1"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AlterJobs;